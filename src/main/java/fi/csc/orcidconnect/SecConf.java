package fi.csc.orcidconnect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@SpringBootApplication
@Configuration
@EnableOAuth2Sso
public class SecConf extends WebSecurityConfigurerAdapter { 

	@Autowired
    private Environment env;

	@Autowired
    private OAuth2ClientContextFilter oauth2ClientContextFilter;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
        //http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
		http.antMatcher("/**")
			.addFilterBefore(oauth2ClientContextFilter, SecurityContextPersistenceFilter.class)
			.authorizeRequests().anyRequest().authenticated();
		
		System.out.println("-----" + oauth2ClientContextFilter.toString() + "-----");
		System.out.println("-----filter cofigured-----");
	}
	
	
	@Bean
	public OAuth2ProtectedResourceDetails resource() {
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setId("gitDetails");
		details.setClientId(env.getProperty("git.clientId"));
		details.setClientSecret(env.getProperty("git.clientSecret"));
		details.setAccessTokenUri(env.getProperty("git.accessTokenUri"));
		details.setUserAuthorizationUri(env.getProperty("git.userAuthorizationUri"));
		//details.setTokenName(env.getProperty("google.authorization.code"));
		String commaSeparatedScopes = env.getProperty("git.scope");
		details.setScope(parseScopes(commaSeparatedScopes));
		details.setPreEstablishedRedirectUri(env.getProperty("git.userAuthorizationUri"));
		//details.setUseCurrentUri(false);
		//details.setAuthenticationScheme(AuthenticationScheme.query);
		details.setClientAuthenticationScheme(AuthenticationScheme.form);
		System.out.println("-----" + details.getId() + "-----");
		System.out.println(details.getClientId());
		System.out.println(details.getClientSecret());
		System.out.println(details.getUserAuthorizationUri());
		System.out.println(details.getAccessTokenUri());
		System.out.println("-----" + details.getScope() + "-----");
		return details;
	}
	
	private List<String> parseScopes(String commaSeparatedScopes) {
        List<String> scopes = new ArrayList<String>();
        Collections.addAll(scopes, commaSeparatedScopes.split(","));
        return scopes;
    }
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(SecConf.class);
		app.run(args);
	}
	
}