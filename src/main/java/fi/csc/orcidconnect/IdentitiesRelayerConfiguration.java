package fi.csc.orcidconnect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.Identifier;
import fi.csc.orcidconnect.push.soap.schema.identitiesdescriptor.IdentityDescriptor;

@Component
@ConfigurationProperties(prefix="my")
public class IdentitiesRelayerConfiguration {

	final private String propertyPrefix = "my.pushInterfaceSpecs.";
	
	// Spring will initialise this automatically
	// based on ConfirugrationProperties-annotation
	List<String> pushInterfaces;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private IdentityFactoryComponent idFactory;

	public List<String> getPushInterfaces() {
		return pushInterfaces;
	}

	public void setPushInterfaces(List<String> pushInterfaces) {
		this.pushInterfaces = pushInterfaces;
	}
	
	@SuppressWarnings("rawtypes")
	public IdentitiesRelayer implPicker(IdentityDescriptor idDescr) {
		String className = implNamePicker(idDescr);
		try {
			Constructor con = Class.forName(className).getConstructor();
			IdentitiesRelayer i = (IdentitiesRelayer) con.newInstance();
			HashMap<String, String> confMap = new HashMap<String, String>();
			for (String key: i.getConfStrs()) {
				confMap.put(key, getConfigString(idDescr, key));
			}
			i.setConfig(confMap);
			i.setIdFactory(idFactory);
			return i;
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String implNamePicker(IdentityDescriptor idDescr) {
		return  getConfigString(idDescr, "relayerImplClass");
	}
	
	private String getConfigString (IdentityDescriptor id, String key) {
		return env.getProperty(propertyPrefix +
				getOrg(id) +
				"." + key
				);
	}
	
	private String getOrg(IdentityDescriptor id) {
		return findOrgByIdP(
				getEppnIssuer(id));
	}
	
	private static String getEppnIssuer (IdentityDescriptor idDescr) {
		Identifier id =
				idDescr.findFirstIdentifierWithFn(Identifier.EPPNFRNAME);
		return id.getIssuer();
	}
	
	private String findOrgByIdP (String entityId) {
		if (isNotInit()) throw new IllegalStateException();
		for (Iterator<String> i = pushInterfaces.iterator(); i.hasNext(); ) {
			String org = i.next();
			String cIdp = env.getProperty(propertyPrefix + org + ".idp");
			if (cIdp.equals(entityId)) return org; 
		}
		return "";
	}
	
	private boolean isNotInit() {
		return pushInterfaces == null;
	}
	
}
