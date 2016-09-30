package fi.csc.orcidconnect;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import fi.csc.orcidconnect.push.soap.schema.cscidmtest.AddValue;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.Association;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.Modify;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.ModifyAttr;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.ObjectFactory;
import fi.csc.orcidconnect.push.soap.schema.cscidmtest.RemoveAllValues;
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
	
    public Modify modifyFactory (IdentityDescriptor id) {
    	ObjectFactory objf = new ObjectFactory();
    	Modify mod = objf.createModify();
    	mod.setClassName("User");
    	Association ass = objf.createAssociation();
    	ass.setContent(id.findFirstIdentifierWithFn(eppnFrName).getIdentifierValue());
    	ass.setState("associated");
    	mod.setAssociation(ass);
    	ModifyAttr modAttr = objf.createModifyAttr();
    	modAttr.setAttrName(orcidFrName);
    	modAttr.setRemoveAllValues(new RemoveAllValues());
    	AddValue addVal = objf.createAddValue();
    	addVal.setValue(id.findFirstIdentifierWithFn(orcidFrName).getIdentifierValue());
    	modAttr.setAddValue(addVal);
    	mod.setModifyAttr(modAttr);
    	return mod;
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
		id.setNameFormat(attrNameFormat);
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

	public void setEppnFrName(String eppnFrName) {
		this.eppnFrName = eppnFrName;
	}

	public void setEppnOid(String eppnOid) {
		this.eppnOid = eppnOid;
	}

	public void setOrcidFrName(String orcidFrName) {
		this.orcidFrName = orcidFrName;
	}

	public void setOrcidOid(String orcidOid) {
		this.orcidOid = orcidOid;
	}

	public void setAttrNameFormat(String attrNameFormat) {
		this.attrNameFormat = attrNameFormat;
	}

	public void setMediatorId(String mediatorId) {
		this.mediatorId = mediatorId;
	}

	public void setIssuerStr(String issuerStr) {
		this.issuerStr = issuerStr;
	}

	public String getOrcidFrName() {
		return orcidFrName;
	}

	
}
