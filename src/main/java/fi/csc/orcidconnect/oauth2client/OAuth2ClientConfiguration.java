package fi.csc.orcidconnect.oauth2client;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.github.vbauer.herald.annotation.Log;

@Component
@ConfigurationProperties(prefix="my.oauth2client")
public class OAuth2ClientConfiguration implements InitializingBean {

	private List<String> providerList;
	
	private String[] providers;
	
	private String providersConfStr;
	
	@Value("${my.oauth2client.defaultProvider}")
	private String defaultProvider;
	
	@Value("${my.oauth2client.specialCase}")
	private String specialCase;

	@Value("${my.oauth2client.callBackBase}")
	private String callBackBase;

	@Autowired
	private Environment env;
	
	@Log
	private Logger logger;
	
	
	public OAuth2ClientConfiguration() {
	}
	
	public void setProviderList(List<String> providerList) {
		this.providerList = providerList;
	}
	
	public List<String> getProviderList() {
		return providerList;
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
		String uriStr = callBackBase + 
				specialCase(provider)+ "login";
		logger.debug("uriStr: " + uriStr);
		logger.debug("provider: " + provider);
		logger.debug("specialCase: " + specialCase);
		return uriStr;
	}
	
	public String getUserInfoUriStr(String provider) {
		return env.getProperty(provider + ".userInfoUri");
	}
	
	public String getScope(String provider) {
		return env.getProperty(provider + ".scope");
	}
	
	public String getSpecialCase() {
		return specialCase;
	}
	
	private String specialCase(String provider) {
		if (provider.equals(specialCase)) {
			return "";
		} else {
			return provider;
		}
	}
}
