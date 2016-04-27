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

	@Autowired
	OAuth2ClientConfiguration conf;
	
	@Log
	private Logger logger;
	
	List<String> orcidAdminList;
	
	public AuthenticationProcessingFilter() {
		super (new AntPathRequestMatcher("/*login"));
		setAuthenticationManager(new NoopAuthenticationManager());
	}
	
	public void setOrcidAdminList (List<String> list) {
		this.orcidAdminList = list;
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
		String attrName = "loginRounds";
		if (req.getSession().getAttribute(attrName) != null) {
			loginRounds = 
					(int) req.getSession().getAttribute(attrName);
			loginRounds++;
			req.getSession().setAttribute(attrName, loginRounds);
		} else {
			loginRounds = 0;
			req.getSession().setAttribute(attrName, loginRounds);
		}
		if (loginRounds > 3) {
			req.getSession().setAttribute(attrName, 0);
			throw new AuthenticationServiceException("login loop or too many " + 
					"subsequent login attempts");
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
		String pattern = ctxPath + "/(\\w*)login";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(reqUri);
		logger.debug("uri: " + reqUri);
		logger.debug("matches: " + String.valueOf(m.matches()));
		logger.debug("context: " + ctxPath);
		String provider;
		try {
			provider = m.group(1);
		} catch (IllegalStateException e) {
			provider = conf.getDefaultProvider();
			logger.debug(e.getMessage() + ". Using default provider.");
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
		if (provider.isEmpty()) { 
		    provider = conf.getSpecialCase();
		}
		OAuth2Token token = OAuth2Client.tokenRequest(conf, provider, req.getParameter("code"));
		if (token != null) {
		    Map<String, Object> map;
		    if (token.hasDetails()) {
			map = token.getDetails(); 
		    } else {
			RestTemplate restTemplate = new RestTemplate();
			String result = restTemplate.getForObject(
					conf.getUserInfoUriStr(provider) +
					"?access_token=" 
					+ token.getAt(), String.class);
			JacksonJsonParser parser = new JacksonJsonParser();
			map = parser.parseMap(result);
		    }
		    List<SimpleGrantedAuthority> authList = 
		    		new ArrayList<SimpleGrantedAuthority>();
		    authList.add(new SimpleGrantedAuthority("ROLE_USER"));
		    if (isAdmin(map)) {
		    	authList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		    }
			OAuth2AuthenticationToken auth = new OAuth2AuthenticationToken(
					authList,
					map);
			return auth;
		} else {
			throw new AuthenticationServiceException("OAuth token error");
		}
	}
	
	private boolean isAdmin (Map<String, Object> authMap) {
	    // TODO: parameterise
		System.out.println("---- " + orcidAdminList.size());
		for (Iterator<String> it = orcidAdminList.iterator(); it.hasNext(); ) {
			System.out.println("---- " + it.next());
		}
	    if (authMap.containsKey("orcid") &&
	    		authMap.get("orcid").equals("0000-0003-0833-4032")) {
	    	return true;
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
