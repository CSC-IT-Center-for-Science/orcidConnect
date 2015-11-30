package fi.csc.orcidconnect;


import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class OrcidconnectApplication {

    @RequestMapping("/principal")
	public Principal principal(Principal principal) {
		System.out.println("principal: " + principal.getClass().getName());
		return principal;
  	}

    @RequestMapping("/isAuthenticated")
    public Map<String, String> hasAuth(Authentication a) {
    	HashMap<String, String> m = new HashMap<String, String>();
    	m.put("isAuthenticated", String.valueOf(a.isAuthenticated()));
    	return m;
    }
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/auth")
	public Map<String, String> auth(OAuth2Authentication a) {
		HashMap<String, String> m = new HashMap<String, String>();
		try {
			HashMap<String, ?> map = (HashMap<String, ?>) a.getUserAuthentication().getDetails();
			for (String k: map.keySet()) {
				m.put(k, String.valueOf(map.get(k)));
				/*if (Boolean.class.isInstance(map.get(k))) {
					m.put(k, (boolean) map.get(k) ? "true" : "false");
				} else {
					m.put(k, (String) map.get(k));
				}*/
			}
	  	} catch (ClassCastException e) {
	  		m = new LinkedHashMap<String, String>();
	  		m.put("error", "cast error");
	  	}
		return m;
	}
	
	@RequestMapping(value = {"/headers", "/shib/headers" })
	public Map<String, String> headers(HttpServletRequest req) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			map.put(key, req.getHeader(key));
		}
		return map;
	}

	@RequestMapping(value = { "/attrs", "/shib/attrs" })
	public Map<String, String> attrs(HttpServletRequest req) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (Enumeration<String> e = req.getAttributeNames(); e.hasMoreElements();) {
			String key = e.nextElement();
			map.put(key, String.valueOf(req.getAttribute(key)));
		}
		map.put("getRemoteUser", req.getRemoteUser());
		return map;
	}
	
	@RequestMapping(value = { "getenv", "/shib/env" })
	public Map<String, String> env(HttpServletRequest req) {
		return System.getenv();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(OrcidconnectApplication.class, args);
	}
	  
}

