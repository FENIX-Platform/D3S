package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.msd.services.impl.Load;
import org.fao.fenix.server.tools.spring.SpringContext;

public class LoadCommons implements org.fao.fenix.msd.services.spi.LoadCommons {

	@Override
	public Response getContactIdentity(HttpServletRequest request, String contactID) {
		try {
			ContactIdentity contact = SpringContext.getBean(Load.class).getContactIdentity(contactID);
			return contact!=null ? Response.ok(contact).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getContactIdentitiesByFullText(HttpServletRequest request, String text) {
		try {
			Collection<ContactIdentity> contacts = SpringContext.getBean(Load.class).getContactIdentities(text);
			return contacts!=null && contacts.size()>0 ? Response.ok(contacts).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getContactIdentitiesByFields(HttpServletRequest request, String institution, String department, String name, String surname, String context) {
		try {
			Collection<ContactIdentity> contacts = SpringContext.getBean(Load.class).getContactIdentities(institution, department, name, surname, context);
			return contacts!=null && contacts.size()>0 ? Response.ok(contacts).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
    @Override
    public Response getPublication(HttpServletRequest request, String publicationID) {
        try {
            Publication publication = SpringContext.getBean(Load.class).getPublication(publicationID);
            return publication!=null ? Response.ok(publication).build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
