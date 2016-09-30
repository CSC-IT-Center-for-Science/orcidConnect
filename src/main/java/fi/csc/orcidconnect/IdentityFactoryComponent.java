package fi.csc.orcidconnect;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

@Component
@ConfigurationProperties(prefix="my")
public class IdentityFactoryComponent {

	private String eppnFrName;
	private String eppnOid;
	private String orcidFrName;
	private String orcidOid;
	private String attrNameFormat;
	private String mediatorId;
	private String issuerStr;
	
	public IdentityFactoryComponent (
			String eppnFrName,
			String eppnOid,
			String orcidFrName,
			String orcidOid,
			String attrNameFormat,
			String mediatorId,
			String issuerStr
			) {
		this.eppnFrName = eppnFrName;
		this.eppnOid = eppnOid;
		this.orcidFrName = orcidFrName;
		this.orcidOid = orcidOid;
		this.attrNameFormat = attrNameFormat;
		this.mediatorId = mediatorId; 
		this.issuerStr = issuerStr;
	}

	public Identifier eppnFactory (String eppnStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(eppnStr);
		id.setFriendlyName(eppnFrName);
		id.setName(eppnOid);
		setBasics(id);
		return id;
	}
	
	public Identifier orcidFactory (String orcidStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(orcidStr);
		id.setFriendlyName(orcidFrName);
		id.setName(orcidOid);
		id.setIssuer(issuerStr);
		setBasics(id);
		return id;
	}
	
	public IdentityDescriptor idPairFactory (String orcidStr, String eppnStr) {
		IdentityDescriptor id = new IdentityDescriptor();
		id.addIdentifier(eppnFactory(eppnStr));
		id.addIdentifier(orcidFactory(orcidStr));
		return id;
	}
	
	private void setBasics(Identifier id) {
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
	
	public String getNameFormat() {
		return attrNameFormat;
	}
	public String getIssuerStr() {
		return issuerStr;
	}
	
}
