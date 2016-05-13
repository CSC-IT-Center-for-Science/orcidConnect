package fi.csc.orcidconnect.push.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.stream.StreamResult;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.schema.csc.ObjectFactory;
import fi.csc.orcidconnect.push.soap.schema.csc.ReceiveRequest;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


public class MockSoapClient implements IdentitiesRelayer {
	
	final String callUrl = "https://demo9650738.mockable.io/mockProvisioningBinding";
	final String schemaPackage = "fi.csc.orcidconnect.push.soap.schema.csc";
	final String soapAction = "http://www.novell.com/provisioning/service/receive";
	UsernamePasswordCredentials creds = new UsernamePasswordCredentials("test", "test");
	
    private void send (String eppnStr, String orcidStr) {
    	
		try {
			URL aUrl = new URL(callUrl);
			CredentialsProvider crProv = new BasicCredentialsProvider();
			crProv.setCredentials(
				new AuthScope(aUrl.getHost(), 443),
				creds);
	
	    	WebServiceTemplate wsTempl = new WebServiceTemplate();
	    	
	    	HttpComponentsMessageSender messageSender = 
	    			new HttpComponentsMessageSender();
	    	messageSender.setCredentials(creds);
	    	
	    	wsTempl.setMessageSender(messageSender);
	
	    	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
	    	marshaller.setPackagesToScan(schemaPackage);
	
	    	wsTempl.setDefaultUri(callUrl);
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
	    	        msg.setSoapAction(soapAction);
	    	    }
	    	});    	
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public boolean relay(IdentityDescriptor idDescr) {
		this.send(
			idDescr.findFirstIdentifierWithFn(
				Identifier.eppnFrName).getIdentifierValue(),
			idDescr.findFirstIdentifierWithFn(
				Identifier.orcidFrName).getIdentifierValue());
		return true;
	}
	
	
}
