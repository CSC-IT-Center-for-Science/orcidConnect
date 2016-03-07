package fi.csc.orcidconnect;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public interface IdentitiesRelayer {
	
	public boolean relay (IdentityDescriptor idDescr, String idPStr);

}
