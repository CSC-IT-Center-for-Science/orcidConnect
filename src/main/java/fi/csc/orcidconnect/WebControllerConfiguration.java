package fi.csc.orcidconnect;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="my.controllerConfig")
public class WebControllerConfiguration {
	
	
	// Spring will initialise this automatically
	// based on ConfirugrationProperties-annotation
	private List<String> shibAttrKeys;
	private List<String> userHiddenAttrs;
	
	private String shibAttrNameIdP;
	private String shibAttrNameEppn;
	private String authNameOrcid;

	public String getShibAttrNameIdP() {
		return shibAttrNameIdP;
	}

	public void setShibAttrNameIdP(String shibAttrNameIdP) {
		this.shibAttrNameIdP = shibAttrNameIdP;
	}

	public String getShibAttrNameEppn() {
		return shibAttrNameEppn;
	}

	public void setShibAttrNameEppn(String shibAttrNameEppn) {
		this.shibAttrNameEppn = shibAttrNameEppn;
	}

	public String getAuthNameOrcid() {
		return authNameOrcid;
	}

	public void setAuthNameOrcid(String authNameOrcid) {
		this.authNameOrcid = authNameOrcid;
	}

	public List<String> getShibAttrKeys() {
		return shibAttrKeys;
	}
	
	// setter is needed to initialise 
	// property field
	public void setShibAttrKeys(List<String> shibAttrKeys) {
		this.shibAttrKeys = shibAttrKeys;
	}

	public List<String> getUserHiddenAttrs() {
		return userHiddenAttrs;
	}

	public void setUserHiddenAttrs(List<String> userHiddenAttrs) {
		this.userHiddenAttrs = userHiddenAttrs;
	}
	

}
