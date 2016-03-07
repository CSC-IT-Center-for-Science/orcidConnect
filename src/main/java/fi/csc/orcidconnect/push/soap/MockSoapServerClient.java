package fi.csc.orcidconnect.push.soap;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import fi.csc.orcidconnect.push.soap.schema.csc.ObjectFactory;
import fi.csc.orcidconnect.push.soap.schema.csc.ReceiveRequest;


public class MockSoapServerClient extends WebServiceGatewaySupport {
	
    public void send (String eppnStr, String orcidStr) {
    	
    	WebServiceTemplate wsTempl = new WebServiceTemplate();

    	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    	marshaller.setPackagesToScan("fi.csc.orcidconnect.push.soap.schema.csc");

    	wsTempl.setDefaultUri("http://klaalo-air13:8088/mockProvisioningBinding");
    	wsTempl.setMarshaller(marshaller);
    	wsTempl.setUnmarshaller(marshaller);
    	
    	ObjectFactory objf = new ObjectFactory();
    	ReceiveRequest req = new ReceiveRequest();
    	req.setArg0(objf.createReceiveRequestArg0(eppnStr));
    	req.setArg1(objf.createReceiveRequestArg1(orcidStr));
    	
    	wsTempl.marshalSendAndReceive(req);    	
    }
	
	
}
