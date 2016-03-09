package fi.csc.orcidconnect.push.rest;

import java.util.Arrays;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.Status;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class MockRestClient extends IdentitiesRelayer {

	@Override
	public boolean relay(IdentityDescriptor idDescr) {
		RestTemplate rt = new RestTemplate();
		rt.setMessageConverters(
				Arrays.asList(
						new MappingJackson2HttpMessageConverter())
				);
		Status stat = rt.postForObject(
				"https://demo9650738.mockable.io/identities",
				idDescr, Status.class);
		return stat.status();
	}

}
