package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.templates.dsd.DSDColumnSubject;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Subjects {

    @GET
    @Path("/{rid}")
    public DSDColumnSubject getSubject(@PathParam("rid") String rid) throws Exception;
    @POST
    public DSDColumnSubject insertSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception;
    @PUT
    public DSDColumnSubject updateSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception;
    @PATCH
    public DSDColumnSubject appendSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception;

}
