package org.fao.fenix.msd.services.spi;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreCommons {

    @POST
    @Path("contact")
    public String newContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) throws Exception;

    @PUT
    @Path("contact")
    public void updateContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) throws Exception;

    @PUT
    @Path("contact/append")
    public void appendContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity) throws Exception;

    @DELETE
    @Path("contact/{contactID}")
    @Consumes()
    public void deleteContactIdentity(HttpServletRequest request, @PathParam("contactID") String contactID) throws Exception;

    @POST
    @Path("publication")
    public String newPublication(@Context HttpServletRequest request, Publication publication) throws Exception;


}