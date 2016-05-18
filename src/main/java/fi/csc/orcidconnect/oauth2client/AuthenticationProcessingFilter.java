package fi.csc.orcidconnect.oauth2client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.github.vbauer.herald.annotation.Log;


@Component
@ConfigurationProperties(prefix="my")
public class AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private final String loginRoundAttrName = "loginRounds";
	
	OAuth2ClientConfiguration conf;
	
	@Log
	private Logger logger;
	
	private List<String> orcidAdminList;
	
	@Autowired
	public AuthenticationProcessingFilter(OAuth2ClientConfiguration conf) {
		super (new AntPathRequestMatcher("/*" + conf.getLoginFilterPathMatcher()));
		this.conf = conf;
		setAuthenticationManager(new NoopAuthenticationManager());
	}
	
	public void setOrcidAdminList(List<String> orcidAdminList) {
		this.orcidAdminList = orcidAdminList;
	}
	
	public List<String> getOrcidAdminList() {
		return this.orcidAdminList;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		String provider = providerSelector(req);
		if (redirectCheck(req)) {
			if (provider.isEmpty()) { 
				provider = conf.getSpecialCase();
			}
			response.sendRedirect(OAuth2Client.authorizationRequest(conf, provider).toString());
			return null;
		}
		return auth(req);
	}
	
	private boolean redirectCheck(HttpServletRequest req) {
		int loginRounds;
		// TODO: final class constants with uppercase separated with underscore
		if (req.getSession().getAttribute(loginRoundAttrName) != null) {
			loginRounds = 
					(int) req.getSession().getAttribute(loginRoundAttrName);
			loginRounds++;
			req.getSession().setAttribute(loginRoundAttrName, loginRounds);
		} else {
			loginRounds = 0;
			req.getSession().setAttribute(loginRoundAttrName, loginRounds);
		}
		if (loginRounds > conf.getLoginRoundLimit()) {
			req.getSession().setAttribute(loginRoundAttrName, 0);
			throw new AuthenticationServiceException("login loop or too many " + 
					"subsequent login attempts - limit set to " +
					conf.getLoginRoundLimit() +
					" current count " + loginRounds);
		}
		if (returning(req)) {
			return false;
		} else {
			return true;
		}
		
	}
	
	private String providerSelector(HttpServletRequest req) {
		String reqUri = req.getRequestURI();
		
		String ctxPath = req.getContextPath();
		String pattern = ctxPath + "/(\\w*)" + conf.getLoginFilterPathMatcher();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(reqUri);
		logger.debug("providerSelector uri: " + reqUri);
		logger.debug("providerSelector matches: " + String.valueOf(m.matches()));
		logger.debug("providerSelector context: " + ctxPath);
		String provider;
		try {
			provider = m.group(1);
		} catch (IllegalStateException e) {
			provider = conf.getDefaultProvider();
			logger.debug(e.getMessage() + ". Using default provider.");
		}
		if (provider.isEmpty()) {
			provider = conf.getSpecialCase();
		}
		return provider;
	}
	
	private boolean returning (HttpServletRequest req) {
		if (req.getParameter("code") != null
				&& !req.getParameter("code").isEmpty()) {
			return true;
		}
		return false;
	}

	private Authentication auth(HttpServletRequest req) {
		String provider = providerSelector(req);
		// assume special provider is used
		// if provider string is empty
		OAuth2Token token = OAuth2Client.tokenRequest(conf, provider, req.getParameter("code"));
		
		// TODO: does nimbus actually check state parameter when user is
		// returning from authentication provider?
		
		if (token != null) {
			// in ORCID API special case id is available
			// with access token
			// no need for another userinfo request
			// this is indicated by token object
		    Map<String, Object> map;
		    if (token.hasDetails()) {
		    	map = token.getDetails(); 
		    	logger.debug("----- provider to map: " +providerSelector(req));
		    } else {
				RestTemplate restTemplate = new RestTemplate();
				// TODO: would stream be more efficient way of handling
				// and storing the result
				// google for better solution
				// this solution could cause weird encoding issues
				String result = restTemplate.getForObject(
						conf.getUserInfoUriStr(provider) +
						"?access_token=" 
						+ token.getAt(), String.class);
				JacksonJsonParser parser = new JacksonJsonParser();
				map = parser.parseMap(result);
		    	logger.debug("----- provider to map: " +providerSelector(req));
		    }
		    List<SimpleGrantedAuthority> authList = 
		    		new ArrayList<SimpleGrantedAuthority>();
		    authList.add(new SimpleGrantedAuthority("ROLE_USER"));
		    // TODO: parametrise ROLE_ADMIN -string (e.g. final field in this class)
		    if (isAdmin(map)) {
		    	authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		    }
		    // TODO: add provider attribute to authMap
			OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
					authList,
					map);
			return auth;
		} else {
			throw new AuthenticationServiceException("OAuth token error");
		}
	}
	
	private boolean isAdmin (Map<String, Object> authMap) {
	    if (orcidAdminList != null && 
	    		authMap.containsKey("orcid")) {
			for (Iterator<String> it = orcidAdminList.iterator(); it.hasNext(); ) {
				String adminId = (String) it.next();
	    		if (authMap.get("orcid").equals(adminId)) {
	    	    	return true;
	    		}
			}
	    }
		return false;
	}

	private static class NoopAuthenticationManager implements AuthenticationManager {

		@Override
		public Authentication authenticate(Authentication authentication)
				throws AuthenticationException {
			throw new UnsupportedOperationException("No authentication should be done with this AuthenticationManager");
		}
		
	}

}