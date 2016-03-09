package fi.csc.orcidconnect.push.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public boolean status() {
		if (status == null) {
			return false;
		}
		return status.equals("identities stored");
	}
	
	@Override
	public String toString() {
		if (status == null) {
			return "";
		} else {
			return status;
		}
	}
	
}
