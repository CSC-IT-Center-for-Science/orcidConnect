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
	private String limitLoginRounds;
	private String loginFilterPathMatcher;
	
	private final String providerConfigPrefix = "my.oauth2client.providerConfig"; 

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
	
	public void setLimitLoginRounds(String limitLoginRounds) {
		this.limitLoginRounds = limitLoginRounds;
	}
	
	public String getLoginFilterPathMatcher() {
		return loginFilterPathMatcher;
	}

	public void setLoginFilterPathMatcher(String loginFilterPathMatcher) {
		this.loginFilterPathMatcher = loginFilterPathMatcher;
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
	
	private String getConfigString(String key, String provider) {
		return env.getProperty(providerConfigPrefix +
				"." + provider + "." + key
				);
	}
	
	public String getAuthUriStr(String provider) {
		return getConfigString("authUriStr", provider);
	}

	public String getClientIdStr(String provider) {
		return getConfigString("clientId", provider);
	}

	public String getClientSecretStr(String provider) {
		return getConfigString("clientSecret", provider);
	}

	public String getTokenUriStr(String provider) {
		return getConfigString("tokenUriStr", provider);
	}

	public String getUserInfoUriStr(String provider) {
		return getConfigString("userInfoUri", provider);
	}

	public String getScope(String provider) {
		return getConfigString("scope", provider);
	}
	
	public String getCallBackURI(String provider) {
		String uriStr = callBackBase + 
				specialCase(provider)+ "login";
		logger.debug("uriStr: " + uriStr);
		logger.debug("provider: " + provider);
		logger.debug("specialCase: " + specialCase);
		return uriStr;
	}
	
	public int getLoginRoundLimit() {
		return Integer.parseInt(limitLoginRounds);
	}
	
	public boolean isShowlogin(String provider) {
		return getConfigString("showLogin", provider) != null;
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
