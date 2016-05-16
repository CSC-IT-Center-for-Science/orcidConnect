package fi.csc.orcidconnect;

import java.util.Map;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public interface IdentitiesRelayer {
	
	public boolean relay(IdentityDescriptor idDescr);
	public String[] getConfStrs();
	public void setConfig(Map<String, String> confMap);
	

}
