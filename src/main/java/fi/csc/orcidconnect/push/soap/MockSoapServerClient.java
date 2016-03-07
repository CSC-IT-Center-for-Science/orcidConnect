package fi.csc.orcidconnect.push.soap;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

import fi.csc.orcidconnect.IdentitiesRelayer;
import fi.csc.orcidconnect.push.soap.schema.csc.ObjectFactory;
import fi.csc.orcidconnect.push.soap.schema.csc.ReceiveRequest;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;


public class MockSoapServerClient extends IdentitiesRelayer {
	
    private void send (String eppnStr, String orcidStr) {
    	
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
