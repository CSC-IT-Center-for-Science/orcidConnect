package fi.csc.orcidconnect.push.soap;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import fi.csc.orcidconnect.push.soap.schema.GetWeatherInformation;
import fi.csc.orcidconnect.push.soap.schema.GetWeatherInformationResponse;
import fi.csc.orcidconnect.push.soap.schema.WeatherDescription;


public class SoapClient {
	
    public void customSendAndReceive() {
    	
    	WebServiceTemplate wsTempl = new WebServiceTemplate();
    	Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    	marshaller.setPackagesToScan("fi.csc.orcidconnect.push.soap.schema");

    	wsTempl.setDefaultUri("http://wsf.cdyne.com/WeatherWS/Weather.asmx");
    	wsTempl.setMarshaller(marshaller);
    	wsTempl.setUnmarshaller(marshaller);

    	GetWeatherInformation req = new GetWeatherInformation();
    	
    	GetWeatherInformationResponse resp = 
			(GetWeatherInformationResponse) wsTempl.marshalSendAndReceive(req, 
			new SoapActionCallback("http://ws.cdyne.com/WeatherWS/GetWeatherInformation"));

    	for (WeatherDescription desc : 
    		resp.getGetWeatherInformationResult().getWeatherDescription()) {
    		System.out.println(desc.getWeatherID() + " | " + desc.getDescription());
     	}
        
    }
	
	
}
