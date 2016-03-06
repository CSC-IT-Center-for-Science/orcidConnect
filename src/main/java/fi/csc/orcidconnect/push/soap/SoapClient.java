package fi.csc.orcidconnect.push.soap;

import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapMessage;

import fi.csc.orcidconnect.push.soap.schema.csc.ObjectFactory;
import fi.csc.orcidconnect.push.soap.schema.csc.ReceiveRequest;


public class SoapClient extends WebServiceGatewaySupport {
	
    public void customSendAndReceive() {
    	
    	WebServiceTemplate wsTempl = new WebServiceTemplate();

    	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    	marshaller.setPackagesToScan("fi.csc.orcidconnect.push.soap.schema.csc");
    	//marshaller.setPackagesToScan("fi.csc.orcidconnect.push.soap.schema");

    	wsTempl.setDefaultUri("http://klaalo-air13:8088/mockProvisioningBinding");
    	//wsTempl.setDefaultUri("http://wsf.cdyne.com/WeatherWS/Weather.asmx");
    	wsTempl.setMarshaller(marshaller);
    	wsTempl.setUnmarshaller(marshaller);
    	
    	ObjectFactory objf = new ObjectFactory();
    	ReceiveRequest req = new ReceiveRequest();
    	req.setArg0(objf.createReceiveRequestArg0("zero"));
    	req.setArg1(objf.createReceiveRequestArg1("one"));
    	//GetWeatherInformation req = new GetWeatherInformation();
    	
    	
    	//StreamResult result = new StreamResult(System.out);
    	//marshaller.marshal(req, result);
    	
    	wsTempl.marshalSendAndReceive(req, new WebServiceMessageCallback() {

    	    public void doWithMessage(WebServiceMessage message) {
    	    	System.out.println("----adding to message");
    	    	SoapMessage msg = ((SoapMessage)message);
    	        msg.setSoapAction("http://www.novell.com/provisioning/service/receive");
    	        msg.getEnvelope().addNamespaceDeclaration("ns2", "http://www.novell.com/provisioning/service");
    	        System.out.println("----- msg:" + msg.getPayloadSource().toString());
    	    }
    	});    	
    	
			//new SoapActionCallback("https://www.novell.com/provisioning/service"));
    			
    	/*GetWeatherInformationResponse resp = 
			(GetWeatherInformationResponse) wsTempl.marshalSendAndReceive(req, 
			new SoapActionCallback("http://ws.cdyne.com/WeatherWS/GetWeatherInformation"));

    	for (WeatherDescription desc : 
    		resp.getGetWeatherInformationResult().getWeatherDescription()) {
    		System.out.println(desc.getWeatherID() + " | " + desc.getDescription());
     	}*/
        
    }
	
	
}
