package fi.csc.orcidconnect.oauth2client;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class OAuth2AuthenticationToken extends AbstractAuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private Map<String, Object> authMap;

	public OAuth2AuthenticationToken(
			Collection<? extends GrantedAuthority> authorities, 
			Map<String, Object> authMap) {
		super(authorities);
		this.authMap = authMap;
		if (this.authMap.containsKey("orcid")) {
		    this.authMap.put("id", this.authMap.get("orcid"));
		}
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return authMap.get("id");
	}
	
	public Map<String, Object> getDetails() {
		return authMap;
	}
	
	@Override
	public String getName() {
		return (String) authMap.get("name");
	}

}
