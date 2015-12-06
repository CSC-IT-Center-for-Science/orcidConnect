package fi.csc.orcidconnect.oauth2client;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationProvider 
	implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if (supports(authentication.getClass()) &&
				((OAuth2AuthenticationToken) authentication)
				.getDetails().containsKey("id")) {
			authentication.setAuthenticated(true);
			return authentication;
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
	}

}
