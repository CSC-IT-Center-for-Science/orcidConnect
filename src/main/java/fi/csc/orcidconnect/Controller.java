package fi.csc.orcidconnect;

import java.security.Principal;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private final String[] shibAttrKeys = { 
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
    
        @SuppressWarnings("unchecked")
	@RequestMapping(value = {"/{pathVar:(git|google|orcidSandbox)}/user"})
	public Map<String, String> auth(Authentication a) {
		HashMap<String, String> m = new HashMap<String, String>();
		List<String> dontShow = Arrays.asList(
						      "access_token",
						      "scope",
						      "token_type",
						      "expires_in"
						      );
		try {
			HashMap<String, ?> map = (HashMap<String, ?>) a.getDetails();
			for (String k: map.keySet()) {
			    if (!dontShow.contains(k)) {
				m.put(k, String.valueOf(map.get(k)));
			    }
			}
	  	} catch (ClassCastException e) {
	  		m = new HashMap<String, String>();
	  		m.put("error", "cast error");
	  	}
		return m;
	}
	
        @SuppressWarnings("unchecked")
	@RequestMapping(value = {"/google/user"})
	public Map<String, String> gauth(Authentication a) {
		HashMap<String, String> m = new HashMap<String, String>();
		List<String> dontShow = Arrays.asList(
						      "access_token",
						      "scope",
						      "token_type",
						      "expires_in"
						      );
		try {
			HashMap<String, ?> map = (HashMap<String, ?>) a.getDetails();
			for (String k: map.keySet()) {
			    if (!dontShow.contains(k)) {
				m.put(k, String.valueOf(map.get(k)));
			    }
			}
	  	} catch (ClassCastException e) {
	  		m = new HashMap<String, String>();
	  		m.put("error", "cast error");
	  	}
		return m;
	}
	
    @RequestMapping("/auth")
	public Principal getUser(Principal principal) {
		return principal;
  	}
    
    @SuppressWarnings("serial")
    @RequestMapping("/isAuthenticated")
    public HashMap<String, String> isAuthenticated(Authentication auth) {
    	if (auth != null) {
	    	boolean isAuth = auth.isAuthenticated();
	    	return new HashMap<String, String>() {
				{ put("isAuthenticated", 
	    				String.valueOf(isAuth)); }
	    	};
    	} else {
    		return new HashMap<String, String>() {
    			{ put("isAuthenticated", "false"); }
    		};
    	}
    }

    @RequestMapping("/shib/user")
    public HashMap<String, String> shibUser(HttpServletRequest req) {
	HashMap<String, String> attrs = new HashMap<String, String>();
	for(String k: shibAttrKeys) {
	    attrs.put(k, String.valueOf(req.getAttribute(k)));
	}
	return attrs;
    }
	
}
