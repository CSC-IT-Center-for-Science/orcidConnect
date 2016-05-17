package fi.csc.orcidconnect.oauth2client;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.github.vbauer.herald.annotation.Log;

@Component
@ConfigurationProperties(prefix="my.oauth2client")
public class OAuth2ClientConfiguration {

	private List<String> providerList;
	
	// automatically initialized properties
	// setters needed
	private String defaultProvider;
	private String specialCase;
	private String callBackBase;
	private String shibSignInPath;

	@Autowired
	private Environment env;
	
	@Log
	private Logger logger;
	
	public OAuth2ClientConfiguration() {
	}
	
	public void setDefaultProvider(String defaultProvider) {
		this.defaultProvider = defaultProvider;
	}

	public void setSpecialCase(String specialCase) {
		this.specialCase = specialCase;
	}

	public void setCallBackBase(String callBackBase) {
		this.callBackBase = callBackBase;
	}
	
	public void setShibSignInPath(String shibSignInPath) {
		this.shibSignInPath = shibSignInPath;
	}

	public String getShibSignInPath() {
		return shibSignInPath;
	}

	public void setProviderList(List<String> providerList) {
		this.providerList = providerList;
	}
	
	public List<String> getProviderList() {
		return providerList;
	}
	
	public String getDefaultProvider() {
		return defaultProvider;
	}
	
	public String getOauthProviderMatcherString() {
		String retStr = "";
		for (String str: providerList) {
			retStr += str + "|";
		}
		retStr += shibSignInPath;
		return retStr;
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
	
	public boolean isShowlogin(String provider) {
		return env.getProperty(provider + ".showLogin") != null;
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
