package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.msd.services.impl.Load;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/cm")
public class LoadCommons implements org.fao.fenix.msd.services.spi.LoadCommons {

	@Override
	public ContactIdentity getContactIdentity(HttpServletRequest request, String contactID) throws Exception {
		return SpringContext.getBean(Load.class).getContactIdentity(contactID);
	}
	@Override
	public Collection<ContactIdentity> getContactIdentitiesByFullText(HttpServletRequest request, String text) throws Exception {
        return SpringContext.getBean(Load.class).getContactIdentities(text);
	}
	@Override
	public Collection<ContactIdentity> getContactIdentitiesByFields(HttpServletRequest request, String institution, String department, String name, String surname, String context) throws Exception {
        return SpringContext.getBean(Load.class).getContactIdentities(institution, department, name, surname, context);
	}
    @Override
    public Publication getPublication(HttpServletRequest request, String publicationID) throws Exception {
        return SpringContext.getBean(Load.class).getPublication(publicationID);
    }

}
