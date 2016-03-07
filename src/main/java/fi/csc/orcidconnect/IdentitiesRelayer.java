package fi.csc.orcidconnect;

import fi.csc.orcidconnect.push.soap.MockSoapServerClient;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public abstract class IdentitiesRelayer {
	
	abstract public boolean relay (IdentityDescriptor idDescr);
	
	public static IdentitiesRelayer implPicker (IdentityDescriptor idDescr) {
		switch (getEppnIssuer(idDescr)) {
		case "https://testidp.funet.fi/idp/shibboleth":
			return new MockSoapServerClient();
		}
		return null;
	}
	
	private static String getEppnIssuer (IdentityDescriptor idDescr) {
		Identifier id =
				idDescr.findFirstIdentifierWithFn(Identifier.eppnFrName);
		return id.getIssuer();
	}
	

}
