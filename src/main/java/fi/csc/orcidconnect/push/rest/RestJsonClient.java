package fi.csc.orcidconnect.push.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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

public class RestJsonClient extends IdentitiesRelayer {
	
	String url;
	
	public RestJsonClient (String callUrl) {
		this.url = callUrl;
	}

	@Override
	public boolean relay(IdentityDescriptor idDescr) {

		UsernamePasswordCredentials creds = new UsernamePasswordCredentials("user", "pwd");
		CredentialsProvider crProv = new BasicCredentialsProvider();
		URL aUrl;
		try {
			aUrl = new URL(url);
			crProv.setCredentials(
				new AuthScope(aUrl.getHost(), 443),
				creds);
			
			
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
					url,
					idDescr, Status.class);
			return stat.status();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false; 
		}
	}
	
}
