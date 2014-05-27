package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.full.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.full.common.Publication;
import org.fao.fenix.d3s.msd.services.impl.Load;

@Path("msd/cm")
public class LoadCommons implements org.fao.fenix.d3s.msd.services.spi.LoadCommons {
    @Context HttpServletRequest request;
    @Inject private Load load;

	@Override
	public ContactIdentity getContactIdentity(String contactID) throws Exception {
		return load.getContactIdentity(contactID);
	}
	@Override
	public Collection<ContactIdentity> getContactIdentitiesByFullText(String text) throws Exception {
        return load.getContactIdentities(text);
	}
	@Override
	public Collection<ContactIdentity> getContactIdentitiesByFields(String institution, String department, String name, String surname, String context) throws Exception {
        return load.getContactIdentities(institution, department, name, surname, context);
	}
    @Override
    public Publication getPublication(String publicationID) throws Exception {
        return load.getPublication(publicationID);
    }

}
