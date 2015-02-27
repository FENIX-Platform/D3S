package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.templates.standard.dsd.DSDColumnSubject;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

public interface Subjects {

    @GET
    @Path("/{rid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public DSDColumnSubject getSubject(@PathParam("rid") String rid) throws Exception;
    @POST
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public DSDColumnSubject insertSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception;
    @PUT
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public DSDColumnSubject updateSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception;
    @PATCH
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public DSDColumnSubject appendSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception;

}
