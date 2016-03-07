package fi.csc.orcidconnect;

import fi.csc.orcidconnect.push.soap.MockSoapServerClient;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class EntityIdIdentityRelayer extends IdentitiesRelayer {

	@Override
	public boolean relay (IdentityDescriptor idDescr) {
		if (getEppnIssuer(idDescr)
				.equals("https://testidp.funet.fi/idp/shibboleth")) {
			MockSoapServerClient sc = new MockSoapServerClient();
			sc.send(
					idDescr.findFirstIdentifierWithFn(
							Identifier.eppnFrName).getIdentifierValue(),
					idDescr.findFirstIdentifierWithFn(
							Identifier.orcidFrName).getIdentifierValue());
			return true;
		}
		return false;
	}
	
	private String getEppnIssuer (IdentityDescriptor idDescr) {
		Identifier id =
				idDescr.findFirstIdentifierWithFn(Identifier.eppnFrName);
		return id.getIssuer();
	}

}
