package fi.csc.orcidconnect.push.rest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	
	private String statusStr;
	private LocalDate latestErrorChange = LocalDate.now();

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
		latestErrorChange = LocalDate.now();
	}
	
	public String getErrorChangeDate() {
		DateTimeFormatter df = DateTimeFormatter.RFC_1123_DATE_TIME;
		return df.format(latestErrorChange);
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
