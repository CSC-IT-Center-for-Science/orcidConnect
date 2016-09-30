package fi.csc.orcidconnect;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

@ConfigurationProperties(prefix="my")
public class IdentityFactory {
	
	// TODO: should be configured
	private static final String mediatorId = "https://connect.tutkijatunniste.fi";

	public static Identifier eppnFactory (String eppnStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(eppnStr);
		id.setFriendlyName(Identifier.EPPNFRNAME);
		id.setName(Identifier.EPPNOID);
		setBasics(id);
		return id;
	}
	
	public static Identifier orcidFactory (String orcidStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(orcidStr);
		id.setFriendlyName(Identifier.ORCIDFRNAME);
		id.setName(Identifier.ORCIDOID);
		// TODO: should be defined dynamically
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
		id.setNameFormat(Identifier.ATTRNAMEFORMAT);
		try {
			id.setMediationInstant(
					DatatypeFactory.newInstance().newXMLGregorianCalendar(
							new GregorianCalendar()));
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}
	
}
