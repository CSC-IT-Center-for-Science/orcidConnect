package fi.csc.orcidconnect.push.rest;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
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
				.addInterceptorFirst(new PreemptiveAuthInterceptor())
				.build();
		
		
		HttpComponentsClientHttpRequestFactory reqFac = 
				new PreemptiveRequestFactory();
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
	
	static class PreemptiveAuthInterceptor implements HttpRequestInterceptor {

		// source: http://stackoverflow.com/a/11868040/2413070
		
	    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
	        AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);

	        // If no auth scheme avaialble yet, try to initialize it
	        // preemptively
	        if (authState.getAuthScheme() == null) {
	            CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(HttpClientContext.CREDS_PROVIDER);
	            HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
	            Credentials creds = credsProvider.getCredentials(new AuthScope(targetHost.getHostName(), targetHost.getPort()));
	            if (creds == null)
	                throw new HttpException("No credentials for preemptive authentication");
	            authState.update(new BasicScheme(), creds);
	        }

	    }

	}	
	
	class PreemptiveRequestFactory extends HttpComponentsClientHttpRequestFactory {
				
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
			localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
			return localcontext;			
		}
		
	}

}
