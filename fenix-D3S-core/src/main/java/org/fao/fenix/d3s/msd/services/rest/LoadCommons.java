package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.common.Publication;
import org.fao.fenix.d3s.msd.services.impl.Load;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

@Path("msd/cm")
public class LoadCommons implements org.fao.fenix.d3s.msd.services.spi.LoadCommons {
    @Context HttpServletRequest request;

	@Override
	public ContactIdentity getContactIdentity(String contactID) throws Exception {
		return SpringContext.getBean(Load.class).getContactIdentity(contactID);
	}
	@Override
	public Collection<ContactIdentity> getContactIdentitiesByFullText(String text) throws Exception {
        return SpringContext.getBean(Load.class).getContactIdentities(text);
	}
	@Override
	public Collection<ContactIdentity> getContactIdentitiesByFields(String institution, String department, String name, String surname, String context) throws Exception {
        return SpringContext.getBean(Load.class).getContactIdentities(institution, department, name, surname, context);
	}
    @Override
    public Publication getPublication(String publicationID) throws Exception {
        return SpringContext.getBean(Load.class).getPublication(publicationID);
    }

}
