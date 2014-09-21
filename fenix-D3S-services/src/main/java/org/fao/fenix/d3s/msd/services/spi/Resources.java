package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Resources {

    @GET
    @Path("/rid/{rid}")
    public ResourceProxy getResource(@PathParam("rid") String rid) throws Exception;
    @GET
    @Path("/{uid}/{version}")
    public ResourceProxy getResourceByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
    @POST
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
    public MeIdentification insertResource(Resource resource) throws Exception;
    @PUT
    public MeIdentification updateResource(Resource resource) throws Exception;
    @PATCH
    public MeIdentification appendResource(Resource resource) throws Exception;


    @GET
    @Path("/metadata/{rid}")
    public Object getMetadata(@PathParam("rid") String rid, @QueryParam("full") Boolean full) throws Exception;
    @GET
    @Path("/metadata/{uid}/{version}")
    public Object getMetadataByUID(@PathParam("uid") String uid, @PathParam("version") String version, @QueryParam("full") Boolean full) throws Exception;
    @POST
    @Path("/metadata")
    public MeIdentification insertMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception;
    @PUT
    @Path("/metadata")
    public MeIdentification updateMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception;
    @PATCH
    @Path("/metadata")
    public MeIdentification appendMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception;


    @GET
    @Path("/data/rid/{rid}")
    public Object getData(@PathParam("rid") String rid) throws Exception;
    @GET
    @Path("/data/{uid}/{version}")
    public Object getDataByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
}
