package fi.csc.orcidconnect;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public class IdentityFactory {
	
	public static Identifier eppnFactory (String eppnStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(eppnStr);
		id.setFriendlyName(Identifier.eppnOid);
		id.setName(Identifier.eppnFrName);
		return id;
	}
	
	public static Identifier orcidFactory (String orcidStr) {
		Identifier id = new Identifier();
		id.setIdentifierValue(orcidStr);
		id.setFriendlyName(Identifier.orcidOid);
		id.setName(Identifier.orcidFrName);
		return id;
	}
	
	public static IdentityDescriptor idPairFactory (String orcidStr, String eppnStr) {
		IdentityDescriptor id = new IdentityDescriptor();
		id.addIdentifier(eppnFactory(eppnStr));
		id.addIdentifier(orcidFactory(orcidStr));
		return id;
	}
	
}
