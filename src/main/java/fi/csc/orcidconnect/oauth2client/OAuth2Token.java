package fi.csc.orcidconnect.oauth2client;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.util.Map;

public class OAuth2Token {
	
	AccessToken myAt;
	RefreshToken myRt;
    Map<String, Object> details;
    String myProvider;

	public String getProvider() {
		return myProvider;
	}

	public OAuth2Token (AccessToken at, RefreshToken rt, String provider) {
		this.myAt = at;
		this.myRt = rt;
		this.myProvider = provider;
	}
	
        public OAuth2Token (AccessToken at, RefreshToken rt, 
        		String provider, Map<String, Object> details) {
	    this(at, rt, provider);
	    this.details = details;
	}

	public boolean hasRt() {
		return this.myRt != null;
	}
	
	public boolean hasAt() {
		return this.myAt != null;
	}

	public AccessToken getAt() {
		return myAt;
	}

	public RefreshToken getRt() {
		return myRt;
	}
	
    public Map<String, Object> getDetails() {
    	return this.details;
    }

    public boolean hasDetails() {
    	return this.details != null;
    }

}
