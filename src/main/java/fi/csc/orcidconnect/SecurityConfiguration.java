package fi.csc.orcidconnect;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import fi.csc.orcidconnect.oauth2client.OAuth2ClientConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter { 

	@Autowired
	AuthenticationProvider oAuth2AuthenticationProvider;
	
	@Autowired
	OAuth2ClientConfiguration oauthConf;

	@Value("${my.oauth2client.specialCase}")
	private String specialCase;

	@Override
    protected void configure(HttpSecurity security) throws Exception {
        security
        .csrf().disable()
	    .logout()
	        .logoutUrl("/logout")
	        .logoutSuccessUrl("/?logout=true")
	        .invalidateHttpSession(true)
	    .and()
	    .authenticationProvider(oAuth2AuthenticationProvider)
        	.exceptionHandling()
        		.authenticationEntryPoint(authEntryPoint())
        	.and()
        	.authorizeRequests()
	    .antMatchers("/", "/**/isAuthenticated", "/**/favicon.ico", "/*login", "/logout")
	    	.permitAll()
	    .regexMatchers("/((git|google|orcidSandbox|shib)/){0,1}user").authenticated()
	    .regexMatchers("/(git|google|orcidSandbox|shib)/signin").authenticated()
        .and().authorizeRequests()
                .antMatchers("/auth", "/mappings", "/shib/trigsoap",
                		"/shib/iddescriptor").authenticated()
                .anyRequest().denyAll()
    	;
        
    }

    private AuthenticationEntryPoint authEntryPoint() {
    	
    	LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints =
    			new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
    	for (String path:
    		oauthConf.getProviders()) {
    		entryPoints.put(
    				getMatcher(path),
    				getEntrypoint(path));
    	}

    	DelegatingAuthenticationEntryPoint authEntry =
    			new DelegatingAuthenticationEntryPoint(entryPoints);
    	authEntry.setDefaultEntryPoint(getEntrypoint(oauthConf.getDefaultProvider()));
    	return authEntry;
    }
    
    private AntPathRequestMatcher getMatcher (String path) {
    	return new AntPathRequestMatcher(
    			"/" + path + "/**");
    }
    
    private LoginUrlAuthenticationEntryPoint getEntrypoint(String path) {
    	return new LoginUrlAuthenticationEntryPoint("/" + 
    			specialCase(path)
    			+ "login");
    }

    
    /**
     * Special case only for Orcid Sandbox api which doesn't
     * have pattern /*login specified for callback URI.
     * This would need email cofrrespondence so easier to
     * do it in code for now.
     * @param provider Providerpath resolved by AuthenticationProcessinfilter
     * @return empty string for special case, otherwise regular provider selection path
     */
	private String specialCase(String provider) {
		if (provider.equals(specialCase)) {
			return "";
		} else {
			return provider;
		}
	}

}
