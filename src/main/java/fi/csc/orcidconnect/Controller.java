package fi.csc.orcidconnect;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


@RestController
public class Controller {
	
	private final String attrName_idp = "Shib-Identity-Provider";
	private final String attrName_eppn = "eppn";
	
	private final String authName_orcid = "orcid";

    private final String[] shibAttrKeys = { 
    		attrName_idp,
    		"Shib-Application-ID",
    		attrName_eppn,
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

	private IdentityDescriptor getIdDescr (Authentication a, HttpServletRequest req) {
		IdentityDescriptor id;
		if (a.isAuthenticated()) {
			@SuppressWarnings("unchecked")
			HashMap<String, ?> map = (HashMap<String, ?>) a.getDetails();
			String orcidStr = String.valueOf(map.get(authName_orcid));
			String eppnStr = String.valueOf(req.getAttribute(attrName_eppn));
			id = IdentityFactory.idPairFactory(orcidStr, eppnStr);
			return id;
		}
		return null;
	}
	
	@RequestMapping("/shib/iddescriptor")
	public IdentityDescriptor idDescriptor (Authentication a, HttpServletRequest req) {
		return getIdDescr(a, req);
	}
	
	@RequestMapping("/shib/trigsoap")
	public List<String> trigSoap(Authentication a, HttpServletRequest req) {
		IdentityDescriptor id = getIdDescr(a, req);
		if (id != null) {
			String idpStr = String.valueOf(req.getAttribute(attrName_idp));
			IdentitiesRelayer relayer = new EntityIdIdentityRelayer();
			if (relayer.relay(id, idpStr)) {
				return Arrays.asList("success");
			} else {
				return Arrays.asList("generic error");
			}
		} else {
			return Arrays.asList("not authenticated");
		}
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
