package fi.csc.orcidconnect.push.rest;

import org.springframework.web.client.RestTemplate;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.Status;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class MockRestClient extends IdentitiesRelayer {

	@Override
	public boolean relay(IdentityDescriptor idDescr) {
		RestTemplate rt = new RestTemplate(); 
		Status stat = rt.postForObject(
				"https://demo9650738.mockable.io/identities",
				idDescr, Status.class);
		return stat.status();
	}

}
