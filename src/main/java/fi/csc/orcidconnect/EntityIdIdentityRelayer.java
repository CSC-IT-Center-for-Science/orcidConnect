package fi.csc.orcidconnect;

import fi.csc.orcidconnect.push.soap.MockSoapServerClient;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class EntityIdIdentityRelayer implements IdentitiesRelayer {

	@Override
	public boolean relay(IdentityDescriptor idDescr, String idpStr) {
		if (idpStr.equals("https://testidp.funet.fi/idp/shibboleth")) {
			MockSoapServerClient sc = new MockSoapServerClient();
			sc.sendToMockServer(
					idDescr.findFirstIdentifierWithFn(
							Identifier.eppnFrName).getIdentifierValue(),
					idDescr.findFirstIdentifierWithFn(
							Identifier.orcidFrName).getIdentifierValue());
			return true;
		}
		return false;
	}

}
