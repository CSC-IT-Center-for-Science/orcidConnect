package fi.csc.orcidconnect;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="my.controllerConfig")
public class WebControllerConfiguration {
	
	
	// Spring will initialise this automatically
	// based on ConfirugrationProperties-annotation
	List<String> shibAttrKeys;

	public List<String> getShibAttrKeys() {
		return shibAttrKeys;
	}
	

}
