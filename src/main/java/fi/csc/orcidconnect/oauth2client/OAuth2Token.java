package fi.csc.orcidconnect.oauth2client;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import java.util.Map;

public class OAuth2Token {
	
	AccessToken myAt;
	RefreshToken myRt;
    Map<String, Object> details;

	public OAuth2Token (AccessToken at, RefreshToken rt) {
		this.myAt = at;
		this.myRt = rt;
	}
	
        public OAuth2Token (AccessToken at, RefreshToken rt, Map<String, Object> details) {
	    this(at, rt);
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
