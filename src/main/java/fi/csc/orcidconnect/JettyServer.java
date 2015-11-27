package fi.csc.orcidconnect;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;


public class JettyServer {
	
	public static AuthorizationCodeFlow authFlow;

	public static void main(String[] args) throws Exception {
		
		String clientId = "40182585572-h880b61pu4ugr7vchso5of46oo73kg2l.apps.googleusercontent.com";
		authFlow = new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(),
				new NetHttpTransport(),
				new JacksonFactory(),
				new GenericUrl("https://accounts.google.com/o/oauth2/token"),
				new BasicAuthentication(clientId, "ueIJuhZiWciPZOFoKrbt0YZo"),
				clientId,
				"https://accounts.google.com/o/oauth2/auth")
				.setCredentialDataStore(
						StoredCredential.getDefaultDataStore(
								new MemoryDataStoreFactory()))
				.build();
		
        Server server = new Server(8080);
        
        HashSessionIdManager idmanager = new HashSessionIdManager();
        server.setSessionIdManager(idmanager);
        
        ContextHandler context = new ContextHandler("/");
        server.setHandler(context);
        
        HashSessionManager manager = new HashSessionManager();
        SessionHandler sessions = new SessionHandler(manager);
        context.setHandler(sessions);
        
        ServletHandler sh = new ServletHandler();
        
        sh.addServletWithMapping(JettyServlet.class, "/ctrl/*");
        sh.addServletWithMapping(OAuthCallbackServlet.class, "/callback/*");

        sessions.setHandler(sh);
        
        String path = JettyServer.class.getClassLoader().getResource("html/").toExternalForm();
        
        ResourceHandler rh = new ResourceHandler();
        rh.setDirectoriesListed(true);
        rh.setWelcomeFiles(new String[] {"index.html"});
        rh.setResourceBase(path);
        
        HandlerList hl = new HandlerList();
        hl.setHandlers(new Handler[] { rh, sessions, new DefaultHandler() });
        
        server.setHandler(hl);
        server.start();
        server.join();
    }

	
}
