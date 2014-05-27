package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.full.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.full.common.Publication;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreCommons {

    @POST
    @Path("contact")
    public String newContactIdentity(ContactIdentity contactIdentity) throws Exception;

    @PUT
    @Path("contact")
    public void updateContactIdentity(ContactIdentity contactIdentity) throws Exception;

    @PUT
    @Path("contact/append")
    public void appendContactIdentity(ContactIdentity contactIdentity) throws Exception;

    @DELETE
    @Path("contact/{contactID}")
    @Consumes()
    public void deleteContactIdentity(@PathParam("contactID") String contactID) throws Exception;

    @POST
    @Path("publication")
    public String newPublication(Publication publication) throws Exception;


}