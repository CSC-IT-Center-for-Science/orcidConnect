package fi.csc.orcidconnect.push.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	
	private String statusStr;

	// false indicates failure
	private boolean failStatus = false;

	public String getStatus() {
		return statusStr;
	}

	public void setStatus(String status) {
		this.statusStr = status;
	}
	
	public boolean status() {
		if (statusStr == null) {
			return false;
		}
		return statusStr.equals("identities stored");
	}
	
	public boolean isFailure() {
		return failStatus;
	}
	
	public void setFailStatus(boolean failStatus) {
		this.failStatus = failStatus;
	}
	
	@Override
	public String toString() {
		if (statusStr == null) {
			return "";
		} else {
			return statusStr;
		}
	}
	
}
