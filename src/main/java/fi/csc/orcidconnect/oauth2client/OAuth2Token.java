package fi.csc.orcidconnect.oauth2client;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

public class OAuth2Token {
	
	AccessToken myAt;
	RefreshToken myRt;
	
	public OAuth2Token (AccessToken at, RefreshToken rt) {
		this.myAt = at;
		this.myRt = rt;
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
	
	
}