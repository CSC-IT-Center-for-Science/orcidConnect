package fi.csc.orcidconnect;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fi.csc.orcidconnect.push.soap.SoapClient;


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
    
	@Autowired
	RequestMappingHandlerMapping handlerMapping;
	
	@RequestMapping("/shib/trigsoap")
	public List<String> trigSoap(Authentication a, HttpServletRequest req) {
		if (a.isAuthenticated()) {
			@SuppressWarnings("unchecked")
			HashMap<String, ?> map = (HashMap<String, ?>) a.getDetails();
			String orcid = String.valueOf(map.get("orcid"));
			String eppn = String.valueOf(req.getAttribute("eppn"));
			System.out.println("----- " + orcid + " | " + eppn);
		}
		SoapClient sc = new SoapClient();
		sc.customSendAndReceive();
		return Arrays.asList("test");
	}

	@RequestMapping("/mappings")
	public List<String> listMappings() {
		ArrayList<String> lis = new ArrayList<String>();
		Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
		for (RequestMappingInfo i: map.keySet()) {
			for (String pat: i.getPatternsCondition().getPatterns()) {
				lis.add(pat);
			}
		}
		return lis;
	}
    
    
	@RequestMapping(value = {"/{pathVar:git|google|orcidSandbox}/user", "/user"}, method = RequestMethod.GET)
	public Map<String, String> auth(Authentication a) {
		HashMap<String, String> m = new HashMap<String, String>();
		List<String> dontShow = Arrays.asList(
						      "access_token",
						      "scope",
						      "token_type",
						      "expires_in"
						      );
		try {
			@SuppressWarnings("unchecked")
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
	
    @RequestMapping(value = "/auth", method = RequestMethod.GET)
	public Principal getUser(Principal principal) {
		return principal;
  	}
    
    @SuppressWarnings("serial")
    @RequestMapping(value = "/isAuthenticated", method = RequestMethod.GET)
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
    
    @SuppressWarnings("serial")
    @RequestMapping(value= "/shib/isAuthenticated", method = RequestMethod.GET)
    public HashMap<String, String> isShibAuthenticated(HttpServletRequest req) {
    	if (req.getAttribute("eppn") == null || 
    			((String) req.getAttribute("eppn")).isEmpty()) {
		return new HashMap<String, String>() {
			{ put("isAuthenticated", "false"); }
			};
    	} else {
    		return new HashMap<String, String>() {
    			{ put("isAuthenticated", "true"); }
    		};
    	}
    }

    @RequestMapping(value = "/shib/user", method = RequestMethod.GET)
    public HashMap<String, String> shibUser(HttpServletRequest req) {
	HashMap<String, String> attrs = new HashMap<String, String>();
	for(String k: shibAttrKeys) {
	    attrs.put(k, String.valueOf(req.getAttribute(k)));
	}
	return attrs;
    }
    
    @RequestMapping(value= "/{pathVar:git|google|orcidSandbox|shib}/signin")
    public void redirectSigned(HttpServletResponse resp) {
    	try {
			resp.sendRedirect("/app/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
}
