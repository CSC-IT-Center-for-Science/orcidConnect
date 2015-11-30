package fi.csc.orcidconnect;


import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

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
	
	private final String[] keys = { 
			"Shib-Identity-Provider",
			"Shib-Application-ID",
			"eppn",
			"persistent-id",
			"Shib-Session-ID",
			"Shib-AuthnContext-Decl",
			"Shib-Authentication-Instant",
			"entitlement",
			"Shib-Assertion-Count",
			"Shib-Session-Index",
			"targeted-id",
			"Shib-AuthnContext-Class",
			"Shib-Cookie-Name",
			"affiliation",
			"REMOTE_USER",
			"Shib-Authentication-Method",
			"unscoped-affiliation"
			};


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
	public Map<String, String> env() {
		return System.getenv();
	}
	
	@RequestMapping(value = { "props", "/shib/props" })
	public Map<String, String> props() {
		HashMap<String, String> map = new HashMap<String, String>();
		Properties p = System.getProperties();
		for (Enumeration<?> e = p.propertyNames(); e.hasMoreElements();) {
			String key = String.valueOf(e.nextElement());
			map.put(key, p.getProperty(key));
		}
		return map;
	}

	@RequestMapping(value = { "/shib/extra" })
	public Map<String, String> extra(HttpServletRequest req) {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String key: this.keys) {
			map.put(key, String.valueOf(req.getAttribute(key)));
		}
		return map;
	}

	public static void main(String[] args) {
		SpringApplication.run(OrcidconnectApplication.class, args);
	}
	  
}

