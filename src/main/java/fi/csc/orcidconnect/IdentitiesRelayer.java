package fi.csc.orcidconnect;

import fi.csc.orcidconnect.push.rest.RestJsonClient;
import fi.csc.orcidconnect.push.soap.MockSoapClient;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public abstract class IdentitiesRelayer {
	
	abstract public boolean relay (IdentityDescriptor idDescr);
	
	public static IdentitiesRelayer implPicker (IdentityDescriptor idDescr) {
		switch (getEppnIssuer(idDescr)) {
		case "https://testidp.funet.fi/idp/shibboleth":
			return new MockSoapClient();
		case "https://idp.testshib.org/idp/shibboleth":
			return new RestJsonClient(
					"https://demo9650738.mockable.io/identities");
		}
		return null;
	}
	
	private static String getEppnIssuer (IdentityDescriptor idDescr) {
		Identifier id =
				idDescr.findFirstIdentifierWithFn(Identifier.eppnFrName);
		return id.getIssuer();
	}
	

}
