package fi.csc.orcidconnect.push.rest;

import java.util.Arrays;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class RestJsonClient implements IdentitiesRelayer {
	
	private final static String restUrl = "restUrl";
	private final static String authPass = "authPass";
	private final static String authUser = "authUser";
	
	private static final String[] confStrs = {
			restUrl,
			authPass,
			authUser
	};
	
	private Map<String, String> config;
	
	@Override
	public void setConfig(Map<String, String> confMap) {
		this.config = confMap;
	}

	@Override
	public final String[] getConfStrs() {
		return RestJsonClient.confStrs;
	}
	
	private boolean checkConfig() {
		for (String s: confStrs) {
			if (!config.containsKey(s)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean relay(IdentityDescriptor idDescr) {

		if (!checkConfig()) {
    		throw new IllegalStateException("Inadequate config");
		}
    	
		UsernamePasswordCredentials creds = 
				new UsernamePasswordCredentials(
						config.get(authUser), 
						config.get(authPass));
		CredentialsProvider crProv = new BasicCredentialsProvider();
		crProv.setCredentials(AuthScope.ANY, creds);
		
		
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultCredentialsProvider(crProv)
				.build();
		

		HttpComponentsClientHttpRequestFactory reqFac = 
				new HttpComponentsClientHttpRequestFactory();
		reqFac.setHttpClient(httpClient);

		RestTemplate rt = new RestTemplate(reqFac);
		rt.setMessageConverters(
				Arrays.asList(
						new MappingJackson2HttpMessageConverter())
				);

		Status stat = rt.postForObject(
				config.get(restUrl),
				idDescr, Status.class);
		return stat.status();
	}

}
