package fi.csc.orcidconnect.oauth2client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.boot.json.JacksonJsonParser;
import java.util.Map;


import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import org.springframework.security.authentication.AuthenticationServiceException;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.AuthorizationRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

public class OAuth2Client {


        private static Logger logger = Logger.getLogger(OAuth2Client.class);


	public static URI authorizationRequest(OAuth2ClientConfiguration conf, String provider) {
    	// The authorisation endpoint of the server
    	URI authzEndpoint = null;
		try {
			authzEndpoint = new URI(conf.getAuthUriStr(provider));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

    	// The client identifier provisioned by the server
    	ClientID clientID = new ClientID(conf.getClientIdStr(provider));

    	// The requested scope values for the token
    	Scope scope = null;
    	if (conf.getScope(provider) != null) {
    		scope = new Scope(conf.getScope(provider));
    	}

    	// The client callback URI, typically pre-registered with the server
    	URI callback = null;
		try {
			callback = new URI(conf.getCallBackURI(provider));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

    	// Generate random state string for pairing the response to the request
    	State state = new State();

    	// Build the request
    	AuthorizationRequest request;
    	if (scope != null) {
	    	request = new AuthorizationRequest.Builder(
	    	    new ResponseType(ResponseType.Value.CODE), clientID).
	    	    scope(scope).
	    	    state(state).
	    	    redirectionURI(callback).
	    	    endpointURI(authzEndpoint).
	    	    build();
    	} else {
	    	request = new AuthorizationRequest.Builder(
		    	    new ResponseType(ResponseType.Value.CODE), clientID).
		    	    state(state).
		    	    redirectionURI(callback).
		    	    endpointURI(authzEndpoint).
		    	    build();
    		
    	}
    	// Use this URI to send the end-user's browser to the server
    	URI requestURI;
		try {
		    URI uri = request.toURI();
		    URIBuilder uBuilder = new URIBuilder(uri);
		    // TODO: parametrise
		    // TODO: this is special setup for Orcid only. What about other providers?
		    uBuilder.addParameter("show_login", "true");
		    requestURI = uBuilder.build();
		} catch (SerializeException | URISyntaxException e) {
		    e.printStackTrace();
		    return null;
		} 
    	
    	return requestURI;
		
	}
	
	public static OAuth2Token tokenRequest(OAuth2ClientConfiguration conf, String provider, String codeStr) {
		try {
			// Construct the code grant from the code obtained from the authz endpoint
			// and the original callback URI used at the authz endpoint
			AuthorizationCode code = new AuthorizationCode(codeStr);
			URI callback;
			callback = new URI(conf.getCallBackURI(provider));
			AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);
	
			// The credentials to authenticate the client at the token endpoint
			ClientID clientID = new ClientID(conf.getClientIdStr(provider));
			Secret clientSecret = new Secret(conf.getClientSecretStr(provider));
			ClientAuthentication clientAuth = new ClientSecretBasic(clientID, clientSecret);
	
			// The token endpoint
			URI tokenEndpoint;
			tokenEndpoint = new URI(conf.getTokenUriStr(provider));
			// Make the token request
			TokenRequest request = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);
			HTTPRequest req = request.toHTTPRequest();
			req.setAccept("application/json");

			HTTPResponse httpResponse = req.send();

			if (provider.equals(conf.getSpecialCase())) {
			    logger.debug("response: " + httpResponse.getContent());
			    return getToken(httpResponse, true);
			}

			return getToken(httpResponse);
			
		} catch (URISyntaxException | SerializeException | IOException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	private static OAuth2Token getToken(HTTPResponse httpResponse) {
		try {
			TokenResponse tokenResponse = TokenResponse.parse(httpResponse);
			if (! tokenResponse.indicatesSuccess()) {
			    // We got an error response...
			    @SuppressWarnings("unused")
				TokenErrorResponse errorResponse = (TokenErrorResponse) tokenResponse;
			}

			AccessTokenResponse successResponse = (AccessTokenResponse) tokenResponse;
			// Get the access token, the server may also return a refresh token
			AccessToken accessToken = successResponse.getAccessToken();
			RefreshToken refreshToken = successResponse.getRefreshToken();	
			return new OAuth2Token(accessToken, refreshToken);

		} catch (ParseException | ClassCastException e) {
			logger.error(httpResponse.getContent());
			throw new AuthenticationServiceException(e.getMessage());
		} 
		
	}
	
    private static OAuth2Token getToken (HTTPResponse httpResponse, boolean detailCase) {
	try {
	    TokenResponse tokenResponse = TokenResponse.parse(httpResponse);
	    if (! tokenResponse.indicatesSuccess()) {
		// We got an error response...
		@SuppressWarnings("unused")
		    TokenErrorResponse errorResponse = (TokenErrorResponse) tokenResponse;
	    }

	    AccessTokenResponse successResponse = (AccessTokenResponse) tokenResponse;
	    // Get the access token, the server may also return a refresh token
	    AccessToken accessToken = successResponse.getAccessToken();
	    RefreshToken refreshToken = successResponse.getRefreshToken();
	    
	    JacksonJsonParser parser = new JacksonJsonParser();
	    Map<String, Object> map = parser.parseMap(httpResponse.getContent());
	    
	    return new OAuth2Token(accessToken, refreshToken, map);

	} catch (ParseException | ClassCastException e) {
	    logger.error(httpResponse.getContent());
	    throw new AuthenticationServiceException(e.getMessage());
	} 
    }
}
