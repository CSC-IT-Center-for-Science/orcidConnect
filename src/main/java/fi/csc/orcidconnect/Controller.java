package fi.csc.orcidconnect;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


@RestController
public class Controller {
	
    @Autowired
    WebControllerConfiguration webConf;
    
	@Autowired
	RequestMappingHandlerMapping handlerMapping;
	
	@Autowired
	Environment env;
	
	@Autowired
	IdentitiesRelayerConfiguration idRelConf;
	
	@RequestMapping("/${my.oauth2client.shibSignInPath}/env.json")
	public Map<String, String> printEnv() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Iterator<?> i =
				((AbstractEnvironment) env).getPropertySources().iterator();
				i.hasNext();
				) {
			PropertySource<?> propS = (PropertySource<?>) i.next();
			if (propS instanceof MapPropertySource) {
				map.putAll(((MapPropertySource) propS).getSource());
			}
		}
		Map<String, String> printMap = new TreeMap<String, String>();
		for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			printMap.put(key, String.valueOf(map.get(key)));
		}
		return printMap;
	}
	

	private IdentityDescriptor getIdDescr (Authentication a, HttpServletRequest req) {
		IdentityDescriptor id = new IdentityDescriptor();
		if (a.isAuthenticated()) {
			@SuppressWarnings("unchecked")
			HashMap<String, ?> map = (HashMap<String, ?>) a.getDetails();
			String orcidStr = String.valueOf(
					map.get(webConf.getAuthNameOrcid())
					);
			String eppnStr = String.valueOf(
					req.getAttribute(webConf.getShibAttrNameEppn())
					);
			Identifier eppnId = IdentityFactory.eppnFactory(eppnStr);
			eppnId.setIssuer(String.valueOf(
					req.getAttribute(webConf.getShibAttrNameIdP()))
					);
			Identifier orcidId = IdentityFactory.orcidFactory(orcidStr);
			id.addIdentifier(orcidId);
			id.addIdentifier(eppnId);
		}
		return id;
	}
	
	@RequestMapping("/${my.oauth2client.shibSignInPath}/iddescriptor.{xml|json}")
	public IdentityDescriptor idDescriptor (Authentication a, HttpServletRequest req) {
		return getIdDescr(a, req);
	}
	
	@RequestMapping("/${my.oauth2client.shibSignInPath}/trigpush")
	public List<String> trigPush(Authentication a, HttpServletRequest req) {
		IdentityDescriptor id = getIdDescr(a, req);
		if (id.getIdentifier().isEmpty()) {
			return Arrays.asList("empty identity");
		} else {
			if (idRelConf != null) {
				IdentitiesRelayer relayer = idRelConf.implPicker(id);
				if (relayer == null) {
					return Arrays.asList("relayerimplementation not found");
				}
				if (relayer.relay(id)) {
					return Arrays.asList("success");
				} else {
					return Arrays.asList("generic error");
				}
			} else {
				return Arrays.asList("missing configuration");
			}
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
    
	@RequestMapping(value = {"/{pathVar:${my.controllerConfig.userMatcherString}}/user", "/user"}, method = RequestMethod.GET)
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
    @RequestMapping(value= "/${my.oauth2client.shibSignInPath}/isAuthenticated", method = RequestMethod.GET)
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

    @RequestMapping(value = "/${my.oauth2client.shibSignInPath}/user", method = RequestMethod.GET)
    public HashMap<String, String> shibUser(HttpServletRequest req) {
	HashMap<String, String> attrs = new HashMap<String, String>();
	for(String k: webConf.getShibAttrKeys()) {
	    attrs.put(k, String.valueOf(req.getAttribute(k)));
	}
	return attrs;
    }
    
    @RequestMapping(value= "/{pathVar:git|google|orcidSandbox|${my.oauth2client.shibSignInPath}}/signin")
    public void redirectSigned(HttpServletResponse resp) {
    	try {
			resp.sendRedirect("/app/");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}
