package fi.csc.orcidconnect;


import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class OrcidconnectApplication {

	  @RequestMapping("/principal")
	  public Principal principal(Principal principal) {
	    return principal;
	  }

	  @RequestMapping("/auth")
	  public Authentication auth(Authentication a) {
	    return a;
	  }

	  public static void main(String[] args) {
        SpringApplication.run(OrcidconnectApplication.class, args);
	  }
	  
}

