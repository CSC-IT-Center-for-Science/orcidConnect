package fi.csc.orcidconnect.push.rest;

import java.text.DateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	
	private String statusStr;
	private Date latestErrorChange = new Date();

	// true indicates failure
	private boolean isError = false;

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
	
	public boolean getIsError() {
		return isError;
	}
	
	public void setIsError(boolean failStatus) {
		this.isError = failStatus;
		latestErrorChange = new Date();
	}
	
	public String getErrorChangeDate() {
		return DateFormat.getDateInstance().format(latestErrorChange);
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
