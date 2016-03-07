package fi.csc.orcidconnect;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class IdentityFactory {
	
	private static final String mediatorId = "https://connect.tutkijatunniste.fi";
	
	public static Identifier eppnFactory (String eppnStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(eppnStr);
		id.setFriendlyName(Identifier.eppnFrName);
		id.setName(Identifier.eppnOid);
		setBasics(id);
		return id;
	}
	
	public static Identifier orcidFactory (String orcidStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(orcidStr);
		id.setFriendlyName(Identifier.orcidFrName);
		id.setName(Identifier.orcidOid);
		id.setIssuer("https://sandbox.orgid.org");
		setBasics(id);
		return id;
	}
	
	public static IdentityDescriptor idPairFactory (String orcidStr, String eppnStr) {
		IdentityDescriptor id = new IdentityDescriptor();
		id.addIdentifier(eppnFactory(eppnStr));
		id.addIdentifier(orcidFactory(orcidStr));
		return id;
	}
	
	private static void setBasics(Identifier id) {
		id.setMediator(mediatorId);
		id.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:uri");
		try {
			id.setMediationInstant(
					DatatypeFactory.newInstance().newXMLGregorianCalendar(
							new GregorianCalendar()));
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
