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
	private String homeLinkEn;
	private String infoLinkEn;
	private String homeLinkFi;
	private String infoLinkFi;

	public void setHomeLinkEn(String homeLink) {
		this.homeLinkEn = homeLink;
	}
	public void setInfoLinkEn(String infoLink) {
		this.infoLinkEn = infoLink;
	}
	public void setHomeLinkFi(String homeLink) {
		this.homeLinkFi = homeLink;
	}
	public void setInfoLinkFi(String infoLink) {
		this.infoLinkFi = infoLink;
	}

	public String getHomeLink(String lang) {
		switch (lang) {
			case "en":
				return homeLinkEn;
			default:
				return homeLinkFi;
		}
	}

	public String getInfoLink(String lang) {
		switch (lang) {
			case "en":
				return infoLinkEn;
			default:
				return infoLinkFi;
		}
	}

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
