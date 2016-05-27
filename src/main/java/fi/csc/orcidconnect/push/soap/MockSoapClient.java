package fi.csc.orcidconnect.push.soap;

import java.io.IOException;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.rest.Status;
import fi.csc.orcidconnect.push.soap.schema.csc.ObjectFactory;
import fi.csc.orcidconnect.push.soap.schema.csc.ReceiveRequest;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


public class MockSoapClient implements IdentitiesRelayer {
	
	private final static String schemaPackage = "schemaPackage"; 
	private final static String soapUrl = "soapUrl"; 
	private final static String soapAction = "soapAction"; 
	private final static String authUser = "authUser"; 
	private final static String authPass = "authPass"; 
	
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
	public Status relay(IdentityDescriptor idDescr) {

		if (!checkConfig()) {
    		throw new IllegalStateException("Inadequate config");
		}
    	
		this.send(
			idDescr.findFirstIdentifierWithFn(
				Identifier.eppnFrName).getIdentifierValue(),
			idDescr.findFirstIdentifierWithFn(
				Identifier.orcidFrName).getIdentifierValue());
		// TODO: dummy return object
		return new Status();
	}	
	
    private void send (String eppnStr, String orcidStr) {
    	
    	WebServiceTemplate wsTempl = new WebServiceTemplate();
    	
    	HttpComponentsMessageSender messageSender = 
    			new HttpComponentsMessageSender();
		
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				config.get(authUser),
				config.get(authPass));
		CredentialsProvider crProv = new BasicCredentialsProvider();
		crProv.setCredentials(AuthScope.ANY, creds);
		
        CloseableHttpClient httpclient = HttpClients.custom()
        		.setDefaultCredentialsProvider(crProv)
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
    	
    	ObjectFactory objf = new ObjectFactory();
    	ReceiveRequest req = new ReceiveRequest();
    	req.setArg0(objf.createReceiveRequestArg0(eppnStr));
    	req.setArg1(objf.createReceiveRequestArg1(orcidStr));
    	
    	wsTempl.marshalSendAndReceive(req, new WebServiceMessageCallback() {
    	    public void doWithMessage(WebServiceMessage message) {
    	    	SoapMessage msg = ((SoapMessage)message);
    	        msg.setSoapAction(config.get(soapAction));
    	    }
    	});    	
    }	
	
}
