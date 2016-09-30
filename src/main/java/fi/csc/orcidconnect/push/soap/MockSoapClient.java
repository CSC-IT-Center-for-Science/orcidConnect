package fi.csc.orcidconnect.push.soap;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import fi.csc.orcidconnect.IdentitiesRelayStatus;
import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.IdentityFactoryComponent;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.BatchResponse;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.Modify;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


public class MockSoapClient implements IdentitiesRelayer {
	
	private final static String schemaPackage = "schemaPackage"; 
	private final static String soapUrl = "soapUrl"; 
	private final static String soapAction = "soapAction"; 
	private final static String authUser = "authUser"; 
	private final static String authPass = "authPass"; 
	private static IdentityFactoryComponent idFactory;
	
	private static final String[] confStrs = {
			schemaPackage,
			soapUrl,
			soapAction,
			authUser,
			authPass
	};
	
	private Map<String, String> config;
	
	
	@Override
	public void setConfig (Map<String, String> confMap) {
		this.config = confMap;
	}
	
	@Override
	public final String[] getConfStrs() {
		return confStrs;
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
	public IdentitiesRelayStatus relay(IdentityDescriptor idDescr) {

		if (!checkConfig()) {
    		throw new IllegalStateException("Inadequate config");
		}
    	
		BatchResponse resp = this.send(idDescr);
		
		IdentitiesRelayStatus retStatus = new IdentitiesRelayStatus();
		retStatus.setIsError(
				!resp.getErrorResponse().getDetail().equals("success"));
		retStatus.setStatus(
				resp.getErrorResponse().getMessage());
		return retStatus;
	}	
	
    private BatchResponse send (IdentityDescriptor id) {
    	
    	WebServiceTemplate wsTempl = new WebServiceTemplate();
    	
    	HttpComponentsMessageSender messageSender = 
    			new HttpComponentsMessageSender();
		
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				config.get(authUser),
				config.get(authPass));
		CredentialsProvider crProv = new BasicCredentialsProvider();
		crProv.setCredentials(AuthScope.ANY, creds);
		
		SSLContextBuilder sslBuilder = new SSLContextBuilder();
		try {
			sslBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		
	        CloseableHttpClient httpclient = HttpClients.custom()
	        		.setDefaultCredentialsProvider(crProv)
	        		.setSSLContext(sslBuilder.build())
	        		.setSSLHostnameVerifier(new NoopHostnameVerifier())
	        		// fix for annoying bug
	        		// (see John Rix's answer: 
	        		// http://stackoverflow.com/questions/3332370/content-length-header-already-present )
	        		.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
	        		.build();
	        
	        messageSender.setHttpClient(httpclient);
	        wsTempl.setMessageSender(messageSender);
	        
	    	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	    	marshaller.setPackagesToScan(config.get(schemaPackage));
	
	    	wsTempl.setDefaultUri(config.get(soapUrl));
	    	wsTempl.setMarshaller(marshaller);
	    	wsTempl.setUnmarshaller(marshaller);
	    	
	    	wsTempl.setFaultMessageResolver(new FaultMessageResolver() {
				
				@Override
				public void resolveFault(WebServiceMessage message) throws IOException {
					StreamResult res = (StreamResult) message.getPayloadResult();
					System.out.println("----- Fault: " + res.getWriter());
					
				}
			});
	    	
	    	Modify mod = idFactory.modifyFactory(id);
	    	
	    	Object unmarsh = wsTempl.marshalSendAndReceive(mod, new WebServiceMessageCallback() {
	    	    public void doWithMessage(WebServiceMessage message) {
	    	    	SoapMessage msg = ((SoapMessage)message);
	    	        msg.setSoapAction(config.get(soapAction));
	    	    }
	    	});
	    	
	    	if (BatchResponse.class.isInstance(unmarsh)) {
	    		return (BatchResponse) unmarsh;
	    	} else {
	    		return null;
	    	}
	    	

		} catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public void setIdFactory(IdentityFactoryComponent idFactory) {
		MockSoapClient.idFactory = idFactory;
	}	
	
}
