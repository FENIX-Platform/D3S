package org.fao.fenix.msd.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.msd.services.impl.Delete;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

public class StoreCommons implements org.fao.fenix.msd.services.spi.StoreCommons {

	
	
	@Override
	public Response newContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) {
		try {
			String contactID = SpringContext.getBean(Store.class).newContactIdentity(contactIdentity);
			return Response.ok(contactID).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response updateContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) {
		try {
			int count =	SpringContext.getBean(Store.class).updateContactIdentity(contactIdentity, false);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response appendContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) {
		try {
			int count =	SpringContext.getBean(Store.class).updateContactIdentity(contactIdentity, true);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteContactIdentity(@Context HttpServletRequest request, String contactID) {
		try {
			int count =	SpringContext.getBean(Delete.class).deleteContactIdentity(contactID);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

    @Override
    public Response newPublication(@Context HttpServletRequest request, Publication publication) {
        try {
            String publicationID = SpringContext.getBean(Store.class).newPublication(publication);
            return Response.ok(publicationID).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

}
