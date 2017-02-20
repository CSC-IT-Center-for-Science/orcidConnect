package fi.csc.orcidconnect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentitiesRelayStatus {
	
	private String statusStr;
	private LocalDateTime latestErrorChange = LocalDateTime.now();

	// true indicates failure
	private boolean isError = false;

	public String getStatus() {
		return statusStr;
	}

	public void setStatus(String status) {
		PolicyFactory policy = Sanitizers.FORMATTING;
		this.statusStr = policy.sanitize(status);
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
		latestErrorChange = LocalDateTime.now();
	}
	
	public String getErrorChangeDate() {
	    DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
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
