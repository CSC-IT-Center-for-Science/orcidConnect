package fi.csc.orcidconnect.push.soap;

import java.io.IOException;

import javax.xml.transform.stream.StreamResult;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.schema.csc.ObjectFactory;
import fi.csc.orcidconnect.push.soap.schema.csc.ReceiveRequest;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


public class MockSoapClient implements IdentitiesRelayer {
	
    private void send (String eppnStr, String orcidStr) {
    	
    	WebServiceTemplate wsTempl = new WebServiceTemplate();

    	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    	marshaller.setPackagesToScan("fi.csc.orcidconnect.push.soap.schema.csc");

    	wsTempl.setDefaultUri("https://demo9650738.mockable.io/mockProvisioningBinding");
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
    	        msg.setSoapAction("http://www.novell.com/provisioning/service/receive");
    	    }
    	});    	
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
