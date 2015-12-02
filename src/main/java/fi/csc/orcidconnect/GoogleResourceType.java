package fi.csc.orcidconnect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
@RequestMapping("/bigg")
public class GoogleResourceType {

	//@Autowired
    //private Environment env;
	//@Autowired
	//private OAuth2ClientContext oauthCtx;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GoogleResourceType.class);
		app.run(args);
	}

	
	@SuppressWarnings("unchecked")
	@RequestMapping("/auth")
	public Map<String, String> auth(OAuth2Authentication a) {
		HashMap<String, String> m = new HashMap<String, String>();
		try {
			HashMap<String, ?> map = (HashMap<String, ?>) a.getUserAuthentication().getDetails();
			for (String k: map.keySet()) {
				m.put(k, String.valueOf(map.get(k)));
				/*if (Boolean.class.isInstance(map.get(k))) {
					m.put(k, (boolean) map.get(k) ? "true" : "false");
				} else {
					m.put(k, (String) map.get(k));
				}*/
			}
	  	} catch (ClassCastException e) {
	  		m = new LinkedHashMap<String, String>();
	  		m.put("error", "cast error");
	  	}
		return m;
	}
	
	/*@Bean
	public OAuth2ProtectedResourceDetails googleResource() {
		AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
		details.setId("google-oauth-client");
		details.setClientId(env.getProperty("google.clientId"));
		details.setClientSecret(env.getProperty("google.client.Secret"));
		details.setAccessTokenUri(env.getProperty("google.accessTokenUri"));
		details.setUserAuthorizationUri(env.getProperty("google.userAuthorizationUri"));
		//details.setTokenName(env.getProperty("google.authorization.code"));
		String commaSeparatedScopes = env.getProperty("google.scope");
		details.setScope(parseScopes(commaSeparatedScopes));
		System.out.println("-----" + details.getScope() + "-----");
		//details.setPreEstablishedRedirectUri(env.getProperty("google.preestablished.redirect.url"));
		//details.setUseCurrentUri(false);
		//details.setAuthenticationScheme(AuthenticationScheme.query);
		//details.setClientAuthenticationScheme(AuthenticationScheme.form);
		System.out.println("-----" + details.getId() + "-----");
		return details;
	}*/
	
	/*@Bean
	public OAuth2Authentication auth (OAuth2AccessToken token) {
		
	}*/
	
    /*private List<String> parseScopes(String commaSeparatedScopes) {
        List<String> scopes = new ArrayList<String>();
        Collections.addAll(scopes, commaSeparatedScopes.split(","));
        return scopes;
    }*/
    
    /*@Bean
    public OAuth2RestTemplate myRestTempl() {
    	return new OAuth2RestTemplate(googleResource(), oauthCtx);
    }*/
    
    /*public class ServletInitializer extends AbstractDispatcherServletInitializer {

		@Override
		protected WebApplicationContext createServletApplicationContext() {
			AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
			context.register(SecurityConfig.class, MyWebSecurityConfiguration.class);
			return context;
		}

		@Override
		protected String[] getServletMappings() {
			return new String[] { "/bigg" };
		}

		@Override
		protected WebApplicationContext createRootApplicationContext() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void onStartup(ServletContext servletContext) throws ServletException {
			super.onStartup(servletContext);
			registerProxyFilter(servletContext, "springSecurityFilterChain");
			registerProxyFilter(servletContext, "oauth2ClientContextFilter");
		}

		private void registerProxyFilter(ServletContext servletContext, String name) {
			System.out.println("---- registering filters ----");
			DelegatingFilterProxy filter = new DelegatingFilterProxy(name);
			filter.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher");
			servletContext.addFilter(name, filter).addMappingForUrlPatterns(null, false, "/**");
		}
    	
    }*/
	
}
