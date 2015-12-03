package fi.csc.orcidconnect;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@EnableOAuth2Sso
//@RequestMapping(value = {"/bigg", "/app"})
public class Controller {

	@SuppressWarnings("unchecked")
	@RequestMapping("/auth")
	public Map<String, String> auth(OAuth2Authentication a) {
		HashMap<String, String> m = new HashMap<String, String>();
		try {
			HashMap<String, ?> map = (HashMap<String, ?>) a.getUserAuthentication().getDetails();
			for (String k: map.keySet()) {
				m.put(k, String.valueOf(map.get(k)));
			}
	  	} catch (ClassCastException e) {
	  		m = new LinkedHashMap<String, String>();
	  		m.put("error", "cast error");
	  	}
		return m;
	}
	
    @RequestMapping("/user")
	public Principal getUser(Principal principal) {
		return principal;
  	}
	
}
