package fi.csc.orcidconnect;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import fi.csc.orcidconnect.oauth2client.OAuth2ClientConfiguration;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.Modify;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


@Controller
public class Defaultcontroller {
	
	public final static String ISAUTH_ENDPOINT = "isAuthenticated";
	public final static String USER_ENDPOINT = "user";
	
    @Autowired
    WebControllerConfiguration webConf;
    
    @Autowired
    OAuth2ClientConfiguration oauthConf;
    
	@Autowired
	RequestMappingHandlerMapping handlerMapping;
	
	@Autowired
	IdentityFactoryComponent idFactory;
	
	@Autowired
	Environment env;
	
	@Autowired
	IdentitiesRelayerConfiguration idRelConf;
	
	@RequestMapping("/")
	private String getRoot(Model model,
			Locale locale) {
		model.addAttribute("homeLink", webConf.getHomeLink(locale.getLanguage()));
		model.addAttribute("infoLink", webConf.getInfoLink(locale.getLanguage()));
		return "index";
	}
	
	@RequestMapping("/${my.oauth2client.shibSignInPath}/env.json")
	@ResponseBody
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
					map.get(idFactory.getOrcidFrName())
					);
			String eppnStr = String.valueOf(
					req.getAttribute(webConf.getShibAttrNameEppn())
					);
			Identifier eppnId = idFactory.eppnFactory(eppnStr);
			eppnId.setIssuer(String.valueOf(
					req.getAttribute(webConf.getShibAttrNameIdP()))
					);
			Identifier orcidId = idFactory.orcidFactory(orcidStr);
			id.addIdentifier(orcidId);
			id.addIdentifier(eppnId);
		}
		return id;
	}
	
	@RequestMapping("/${my.oauth2client.shibSignInPath}/iddescriptor.{xml|json}")
	@ResponseBody
	public IdentityDescriptor idDescriptor (Authentication a, HttpServletRequest req) {
		return getIdDescr(a, req);
	}
	
	@RequestMapping("/${my.oauth2client.shibSignInPath}/modify.{xml|json}")
	@ResponseBody
	public Modify modifyObject (Authentication a, HttpServletRequest req) {
		IdentityDescriptor id = getIdDescr(a, req);
		return idFactory.modifyFactory(id);
	}
	
	@RequestMapping("/${my.oauth2client.shibSignInPath}/trigpush")
	@ResponseBody
	public HashMap<String, String> trigPush(Authentication a, HttpServletRequest req) {
		final String statusStr = "isError";
		final String descrStr = "description";
		final String errorStr = "true";
		final String timeStampField = "timeStamp";
		final HashMap<String, String> retMap = new HashMap<String, String>();
		IdentityDescriptor id = getIdDescr(a, req);
		if (id.getIdentifier().isEmpty()) {
			retMap.put(statusStr, "error");
			retMap.put(descrStr, "empty identity");
		} else {
			if (idRelConf == null) {
				retMap.put(statusStr, errorStr);
				retMap.put(descrStr, "missing configuration");
				return retMap;
			}
			// NOTE: consider storing configuration and state information more 
			// in IdentityDescriptor object rather than bouncing separate
			// status and configuration objects back and forth from 
			// business logic implementation classes
			IdentitiesRelayer relayer = idRelConf.implPicker(id);
			if (relayer == null) {
				retMap.put(statusStr, errorStr);
				retMap.put(descrStr, "relayerimplementation not found");
				return retMap;
			}
			IdentitiesRelayStatus stat = relayer.relay(id);
			retMap.put(statusStr, String.valueOf(stat.getIsError()));
			retMap.put(descrStr, stat.getStatus());
			retMap.put(timeStampField, stat.getErrorChangeDate());
		}
		return retMap;
	}

	@RequestMapping("/mappings")
	@ResponseBody
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
    
	@RequestMapping(value = 
		{"/{pathVar:${my.controllerConfig.userMatcherString}}/" + USER_ENDPOINT,
				"/" + USER_ENDPOINT}, method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> auth(Authentication a) {
		HashMap<String, String> m = new HashMap<String, String>();
		List<String> dontShow = webConf.getUserHiddenAttrs();
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
	@ResponseBody
	public Principal getUser(Principal principal) {
		return principal;
  	}
    
    @SuppressWarnings("serial")
    @RequestMapping(value = "/" + ISAUTH_ENDPOINT, method = RequestMethod.GET)
	@ResponseBody
    public HashMap<String, String> isAuthenticated(Authentication auth) {
    	if (auth != null) {
	    	boolean isAuth = auth.isAuthenticated();
	    	return new HashMap<String, String>() {
				{ put(ISAUTH_ENDPOINT, 
	    				String.valueOf(isAuth)); }
	    	};
    	} else {
    		return new HashMap<String, String>() {
    			{ put(ISAUTH_ENDPOINT, "false"); }
    		};
    	}
    }
    
    @SuppressWarnings("serial")
    @RequestMapping(value= "/${my.oauth2client.shibSignInPath}/" + ISAUTH_ENDPOINT, method = RequestMethod.GET)
	@ResponseBody
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

    @RequestMapping(value = "/${my.oauth2client.shibSignInPath}/" + USER_ENDPOINT, method = RequestMethod.GET)
	@ResponseBody
    public HashMap<String, String> shibUser(HttpServletRequest req) {
    	// NOTE: make note about new convention in specifying and initializing (local) fields
		final HashMap<String, String> attrs = new HashMap<String, String>();
		for(String k: webConf.getShibAttrKeys()) {
		    attrs.put(k, String.valueOf(req.getAttribute(k)));
		}
		return attrs;
    }
    
    @RequestMapping(value= "/{pathVar:${my.controllerConfig.userMatcherString}|${my.oauth2client.shibSignInPath}}/signin")
	@ResponseBody
    public void redirectSigned(HttpServletResponse resp) {
    	try {
			resp.sendRedirect("/app/");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}
