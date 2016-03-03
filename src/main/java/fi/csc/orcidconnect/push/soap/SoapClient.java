package fi.csc.orcidconnect.push.soap;

import java.io.StringReader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.client.core.WebServiceTemplate;

public class SoapClient {
	
	private static final String MESSAGE =
	        "<message xmlns=\"http://tempuri.org\">Hello Web Service World</message>";

	private final WebServiceTemplate wsTempl = new WebServiceTemplate();
	
	
	// https://spring.io/guides/gs/consuming-web-service/
    public void customSendAndReceive() {
        StreamSource source = new StreamSource(new StringReader(MESSAGE));
        StreamResult result = new StreamResult(System.out);
        wsTempl.sendSourceAndReceiveToResult("http://wsf.cdyne.com/WeatherWS/Weather.asmx/GetWeatherInformation",
        //wsTempl.sendSourceAndReceiveToResult("http://klaalo-air13:8088/mockProvisionBinding/receive",
            source, result);
    }
	
	
}
