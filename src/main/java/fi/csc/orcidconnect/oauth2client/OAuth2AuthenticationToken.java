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
	public static final String ORCIDKEYSTR = "orcid";
	public static final String IDSTR = "id";

	public OAuth2AuthenticationToken(
			Collection<? extends GrantedAuthority> authorities, 
			Map<String, Object> authMap) {
		super(authorities);
		this.authMap = authMap;
		if (this.authMap.containsKey(ORCIDKEYSTR)) {
		    this.authMap.put(IDSTR, this.authMap.get(ORCIDKEYSTR));
		}
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return authMap.get(IDSTR);
	}
	
	public Map<String, Object> getDetails() {
		return authMap;
	}
	
	@Override
	public String getName() {
		return (String) authMap.get("name");
	}

}
