package org.fao.fenix.msd.services.spi;

import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("msd/cm")
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreCommons {

    @POST
    @Path("contact")
    public Response newContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity);

    @PUT
    @Path("contact")
    public Response updateContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity);

    @PUT
    @Path("contact/append")
    public Response appendContactIdentity(@Context HttpServletRequest request, ContactIdentity contactIdentity);

    @DELETE
    @Path("contact/{contactID}")
    @Consumes()
    public Response deleteContactIdentity(HttpServletRequest request, @PathParam("contactID") String contactID);

    @POST
    @Path("publication")
    public Response newPublication(@Context HttpServletRequest request, Publication publication);


}