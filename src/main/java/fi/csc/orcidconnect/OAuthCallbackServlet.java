package fi.csc.orcidconnect;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


public class OAuthCallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	@Override
	protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
		url.setRawPath("/callback/");
		System.out.println("OAuthCallbackServlet: getRecirectUri: " + url.toString());
		return url.toString();
	}

	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id = req.getSession().getId(); 
		System.out.println("OAuthCallbackServlet: getUserId: " + req.getSession().getId());
		return id;
	}
	
	@Override
	protected void onSuccess(HttpServletRequest req, javax.servlet.http.HttpServletResponse resp, com.google.api.client.auth.oauth2.Credential credential) throws ServletException ,IOException {
		String at = credential.getAccessToken();
		String rt = credential.getRefreshToken();
		
		/*GoogleIdTokenVerifier ver = new GoogleIdTokenVerifier(
				new NetHttpTransport(),
				new JacksonFactory());
		try {
			System.out.println(ver.verify(at));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		System.out.println("OAuthCallbackServlet success. at: " + at);
		System.out.println("refresh: " + rt);
		
		resp.sendRedirect("/");
	};

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
		return JettyServer.authFlow;
		/*String clientId = "40182585572-h880b61pu4ugr7vchso5of46oo73kg2l.apps.googleusercontent.com";
		// TODO Auto-generated method stub
		System.out.println("OAuthCallbackServlet: initializeFlow");
		return new AuthorizationCodeFlow.Builder(
				BearerToken.authorizationHeaderAccessMethod(),
				new NetHttpTransport(),
				new JacksonFactory(),
				new GenericUrl("https://accounts.google.com/o/oauth2/token"),
				new BasicAuthentication(clientId, "ueIJuhZiWciPZOFoKrbt0YZo"),
				clientId,
				"https://accounts.google.com/o/oauth2/auth").build();*/
	}

}
