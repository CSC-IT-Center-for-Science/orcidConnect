package fi.csc.orcidconnect;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.valves.RemoteIpValve;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatAjpConfig {

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
	    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
	    tomcat.addAdditionalTomcatConnectors(createConnector());
	    tomcat.addContextValves(createRemoteIpValves());
	    return tomcat;
	}

	private static RemoteIpValve createRemoteIpValves() {
	    RemoteIpValve remoteIpValve = new RemoteIpValve();
	    remoteIpValve.setRemoteIpHeader("x-forwarded-for");
	    remoteIpValve.setProtocolHeader("x-forwarded-proto");
	    return remoteIpValve;
	}

	private static Connector createConnector() {
	    Connector connector = new Connector("AJP/1.3");
	    connector.setPort(8009);
	    return connector;
	}	
	
}
