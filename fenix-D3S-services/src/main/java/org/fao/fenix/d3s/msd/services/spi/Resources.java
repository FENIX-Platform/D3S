package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Resources {

    @GET
    @Path("/{rid}")
    public ResourceProxy getResource(@PathParam("rid") String rid) throws Exception;
    @POST
    public ResourceProxy insertResource(Resource resource) throws Exception;
    @PUT
    public ResourceProxy updateResource(Resource resource) throws Exception;
    @PATCH
    public ResourceProxy appendResource(Resource resource) throws Exception;


    @GET
    @Path("/metadata/{rid}")
    public Object getMetadata(@PathParam("rid") String rid) throws Exception;
    @POST
    @Path("/metadata")
    public Object insertMetadata(MeIdentification metadata) throws Exception;
    @PATCH
    @Path("/metadata")
    public Object updateMetadata(MeIdentification metadata) throws Exception;
    @PUT
    @Path("/metadata")
    public Object appendMetadata(MeIdentification metadata) throws Exception;


    @GET
    @Path("/data/{rid}")
    public Object getData(@PathParam("rid") String rid) throws Exception;
}
