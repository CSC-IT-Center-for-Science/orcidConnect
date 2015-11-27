package fi.csc.orcidconnect;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class JettyServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	
	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) {
		resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}
}
	
