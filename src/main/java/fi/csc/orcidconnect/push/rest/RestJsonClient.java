package fi.csc.orcidconnect.push.rest;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.springframework.http.HttpMethod;
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
		
		URL url;
		try {
			url = new URL(config.get(restUrl));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		HttpComponentsClientHttpRequestFactory reqFac = 
				new LocalRequestFactory();
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
	
	class LocalRequestFactory extends HttpComponentsClientHttpRequestFactory {
				
		@SuppressWarnings("deprecation")
		@Override
		protected BasicHttpContext createHttpContext (HttpMethod httpMethod, URI uri) {
			AuthCache authCache = new BasicAuthCache();
			BasicScheme basicAuth = new BasicScheme();
			int port;
			if (uri.getPort() < 0) {
				if (uri.getScheme().equals("http")) {
					port = 80;
				} else {
					port = 443;
				}
			} else {
				port = uri.getPort();
			}
			HttpHost targetHost = new HttpHost(uri.getHost(), port);
			authCache.put(targetHost, basicAuth);
			
			System.out.println("----- " + uri.getHost().toString() + " : " + String.valueOf(port));
		 
			BasicHttpContext localcontext = new BasicHttpContext();
			localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
			return localcontext;			
		}
		
	}

}
