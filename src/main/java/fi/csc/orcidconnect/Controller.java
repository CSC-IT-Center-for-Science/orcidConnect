package fi.csc.orcidconnect;

import java.security.Principal;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@SuppressWarnings("unchecked")
	@RequestMapping(value = {"/{path}/user"})
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
	
}
