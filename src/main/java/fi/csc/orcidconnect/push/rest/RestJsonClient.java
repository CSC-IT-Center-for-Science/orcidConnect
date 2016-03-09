package fi.csc.orcidconnect.push.rest;

import java.util.Arrays;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class RestJsonClient extends IdentitiesRelayer {
	
	String url;
	
	public RestJsonClient (String callUrl) {
		this.url = callUrl;
	}

	@Override
	public boolean relay(IdentityDescriptor idDescr) {
		RestTemplate rt = new RestTemplate();
		rt.setMessageConverters(
				Arrays.asList(
						new MappingJackson2HttpMessageConverter())
				);
		Status stat = rt.postForObject(
				url,
				idDescr, Status.class);
		return stat.status();
	}

}
