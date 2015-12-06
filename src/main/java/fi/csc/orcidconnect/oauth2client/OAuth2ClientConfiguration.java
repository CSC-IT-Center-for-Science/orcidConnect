package fi.csc.orcidconnect.oauth2client;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class OAuth2ClientConfiguration implements InitializingBean {

	private String[] providers;
	
	@Value("${my.oauth2client.providerList}")
	private String providersConfStr;
	
	@Value("${my.oauth2client.defaultProvider}")
	private String defaultProvider;
	
	@Autowired
	private Environment env;
	
	public OAuth2ClientConfiguration() {
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.providers = providersConfStr.split(",");
	}
	
	public String getDefaultProvider() {
		return defaultProvider;
	}
	
	public String[] getProviders() {
		return providers;
	}
	
	public String getAuthUriStr(String provider) {
		return env.getProperty(provider + ".authUriStr");
	}

	public String getClientIdStr(String provider) {
		return env.getProperty(provider + ".clientId");
	}

	public String getClientSecretStr(String provider) {
		return env.getProperty(provider + ".clientSecret");
	}

	public String getTokenUriStr(String provider) {
		return env.getProperty(provider + ".tokenUriStr");
	}

	public String getCallBackURI(String provider) {
		return "http://localhost:8080/"+ provider + "login";
	}
	
	public String getUserInfoUriStr(String provider) {
		return env.getProperty(provider + ".userInfoUri");
	}
	
	public String getScope(String provider) {
		return env.getProperty(provider + ".scope");
	}
}
