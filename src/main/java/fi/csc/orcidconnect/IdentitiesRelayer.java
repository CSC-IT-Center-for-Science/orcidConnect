package fi.csc.orcidconnect;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

public abstract class IdentitiesRelayer {
	
	public static IdentitiesRelayer implPicker (IdentityDescriptor idDescr) {
		switch (getEppnIssuer(idDescr)) {
		case "https://testidp.funet.fi/idp/shibboleth":
			return new EntityIdIdentityRelayer();
		}
		return null;
	}
	
	abstract public boolean relay (IdentityDescriptor idDescr);
	
	private static String getEppnIssuer (IdentityDescriptor idDescr) {
		Identifier id =
				idDescr.findFirstIdentifierWithFn(Identifier.eppnFrName);
		return id.getIssuer();
	}
	

}
