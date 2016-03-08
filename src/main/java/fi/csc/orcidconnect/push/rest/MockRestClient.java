package fi.csc.orcidconnect.push.rest;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class MockRestClient extends IdentitiesRelayer {

	@Override
	public boolean relay(IdentityDescriptor idDescr) {
		// TODO Auto-generated method stub
		return false;
	}

}
