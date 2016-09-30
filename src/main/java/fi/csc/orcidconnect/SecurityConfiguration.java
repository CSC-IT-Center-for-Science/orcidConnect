package fi.csc.orcidconnect;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Override
    protected void configure(HttpSecurity security) throws Exception {
        security
        // TODO: voiko laittaa csrf:n takaisin päälle?
        .csrf().disable()
	    .logout()
	        .logoutUrl("/logout")
	        .logoutSuccessUrl("/?logout=true")
	        .invalidateHttpSession(true)
	    .and()
	    .authenticationProvider(oAuth2AuthenticationProvider)
        	.exceptionHandling()
        		.authenticationEntryPoint(authEntryPoint())
    	.and().authorizeRequests()
	    .antMatchers("/",
	    		"/**/isAuthenticated",
	    		"/**/favicon.ico",
	    		"/*" + oauthConf.getLoginFilterPathMatcher(),
	    		"/logout",
	    		// TODO: user-endpoint -address could be parametrised
	    		"/" + oauthConf.getShibSignInPath() + "/user")
	    	.permitAll()
	    .regexMatchers("/((" + 
	    	oauthConf.getOauthProviderMatcherString() +
	    	")/){0,1}user")
	    	.authenticated()
	    .regexMatchers("/(" +
	    	oauthConf.getOauthProviderMatcherString() +
	    	")/signin")
	    	.authenticated()
	    .antMatchers("/" + 
	    	oauthConf.getShibSignInPath() + "/env.json",
    		"/auth",
    		"/mappings")
	    	// TODO: parametrise (see AuthenticationProcessingFilter)
	    	.hasAuthority("ROLE_ADMIN")
        .and().authorizeRequests()
                .antMatchers(
                		"/" + oauthConf.getShibSignInPath() + "/trigpush",
                		"/" + oauthConf.getShibSignInPath() + "/iddescriptor.xml",
                		"/" + oauthConf.getShibSignInPath() + "/iddescriptor.json",
        				"/" + oauthConf.getShibSignInPath() + "/modify.json",
						"/" + oauthConf.getShibSignInPath() + "/modify.xml")
                	.authenticated()
                .anyRequest().denyAll()
    	;
        
    }

    private AuthenticationEntryPoint authEntryPoint() {
    	
    	LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints =
    			new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
    	for (String path:
    		oauthConf.getProviderList()) {
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
    			+ oauthConf.getLoginFilterPathMatcher());
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
		if (provider.equals(oauthConf.getSpecialCase())) {
			return "";
		} else {
			return provider;
		}
	}

}
