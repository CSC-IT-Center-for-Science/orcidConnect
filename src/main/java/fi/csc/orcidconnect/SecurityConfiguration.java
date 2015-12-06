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
        	.authenticationProvider(oAuth2AuthenticationProvider)
        	.exceptionHandling()
        		.authenticationEntryPoint(authEntryPoint())
        	.and()
        	.authorizeRequests()
            	.antMatchers("/", "/isAuthenticated", "/**/favicon.ico", "/*login").permitAll()
            .and().authorizeRequests()
                .antMatchers("/*/user", "/auth").authenticated()
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
    	return authEntry;
    }
    
    private AntPathRequestMatcher getMatcher (String path) {
    	return new AntPathRequestMatcher(
    			"/" + path + "/**");
    }
    
    private LoginUrlAuthenticationEntryPoint getEntrypoint(String path) {
    	return new LoginUrlAuthenticationEntryPoint("/" + path + "login");
    }


}