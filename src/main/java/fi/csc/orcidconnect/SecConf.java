package fi.csc.orcidconnect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableOAuth2Sso
public class SecConf extends WebSecurityConfigurerAdapter { 

	@Autowired
    private Environment env;

	//@Autowired
    //private OAuth2ClientContextFilter oauth2ClCtxFilter;
	
	//@Autowired
	//private OAuth2RestTemplate restTempl;

    @Autowired
    private OAuth2ClientContext oauth2ClientContext;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
        //http.antMatcher("/**").authorizeRequests().anyRequest().authenticated();
		http.antMatcher("/**")
			.addFilterBefore(ctxFilter(), SecurityContextPersistenceFilter.class)
			.authorizeRequests().antMatchers("/", "/index.html").permitAll()
			.and()
			.authorizeRequests().anyRequest().authenticated()
			//.authorizeRequests().antMatchers("/app/**", "/bigg/**").authenticated();
			;

		//System.out.println("-----" + oauth2ClCtxFilter.toString() + "-----");
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
	
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices tokenServices = new RemoteTokenServices();
        OAuth2ProtectedResourceDetails resource = resource();

        tokenServices.setClientId(resource.getClientId());
        tokenServices.setClientSecret(resource.getClientSecret());
        tokenServices.setCheckTokenEndpointUrl(resource.getAccessTokenUri());

        return tokenServices;
    }
	
	public OAuth2RestTemplate oAuth2RestTemplate(OAuth2ProtectedResourceDetails resourceDetails) {
		return new OAuth2RestTemplate(resourceDetails, oauth2ClientContext);
	}
	
	@Bean
    public OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter(OAuth2RestOperations restTemplate, ResourceServerTokenServices tokenServices) {
        OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter = new OAuth2ClientAuthenticationProcessingFilter("/gitlogin");
        authenticationProcessingFilter.setRestTemplate(restTemplate);
        authenticationProcessingFilter.setTokenServices(tokenServices);
        System.out.println("-----authenticationProcessingFilter-----");
        return authenticationProcessingFilter;
    }
	
	@Bean
    public OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter() {
        OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter = new OAuth2ClientAuthenticationProcessingFilter("/gitlogin");
        authenticationProcessingFilter.setRestTemplate(oAuth2RestTemplate(resource()));
        authenticationProcessingFilter.setTokenServices(tokenServices());
        System.out.println("-----authenticationProcessingFilter-----");
        return authenticationProcessingFilter;
    }
	
	@Bean
	public OAuth2ClientContextFilter ctxFilter() {
		OAuth2ClientContextFilter filter = new OAuth2ClientContextFilter(); 
		return filter;
	}
	
	@Bean 
	protected AuthenticationEntryPoint authenticationEntryPoint() {
		OAuth2AuthenticationEntryPoint ep = new OAuth2AuthenticationEntryPoint();
		ep.setRealmName("/gitlogin");
		System.out.println("-----authenticationEntypoint-----");
		return ep;
	}
	
    @Bean
    public OAuth2WebSecurityExpressionHandler webSecurityExpressionHandler() {
        return new OAuth2WebSecurityExpressionHandler();
    }
	
	private List<String> parseScopes(String commaSeparatedScopes) {
        List<String> scopes = new ArrayList<String>();
        Collections.addAll(scopes, commaSeparatedScopes.split(","));
        return scopes;
    }

}