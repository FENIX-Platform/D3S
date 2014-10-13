package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.templates.identification.DSD;
import org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Resources {

    @GET
    @Path("/rid/{rid}")
    public ResourceProxy getResource(@PathParam("rid") String rid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd) throws Exception;
    @GET
    @Path("/uid/{uid}")
    public ResourceProxy getResourceByUID(@PathParam("uid") String uid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd) throws Exception;
    @GET
    @Path("/{uid}/{version}")
    public ResourceProxy getResourceByUID(@PathParam("uid") String uid, @PathParam("version") String version, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd) throws Exception;
    @POST
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
    public MeIdentification insertResource(Resource resource) throws Exception;
    @PUT
    public MeIdentification updateResource(Resource resource) throws Exception;
    @PATCH
    public MeIdentification appendResource(Resource resource) throws Exception;
    @DELETE
    @Path("/rid/{rid}")
    public void deleteResource(@PathParam("rid") String rid) throws Exception;
    @DELETE
    @Path("/uid/{uid}")
    public void deleteResourceByUID(@PathParam("uid") String uid) throws Exception;
    @DELETE
    @Path("/{uid}/{version}")
    public void deleteResourceByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;


    @GET
    @Path("/metadata/rid/{rid}")
    public Object getMetadata(@PathParam("rid") String rid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd) throws Exception;
    @GET
    @Path("/metadata/uid/{uid}")
    public Object getMetadataByUID(@PathParam("uid") String uid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd) throws Exception;
    @GET
    @Path("/metadata/{uid}/{version}")
    public Object getMetadataByUID(@PathParam("uid") String uid, @PathParam("version") String version, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd) throws Exception;
    @POST
    @Path("/metadata")
    public MeIdentification insertMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception;
    @PUT
    @Path("/metadata")
    public MeIdentification updateMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception;
    @PATCH
    @Path("/metadata")
    public MeIdentification appendMetadata(org.fao.fenix.commons.msd.dto.full.MeIdentification metadata) throws Exception;
    @DELETE
    @Path("/metadata/rid/{rid}")
    public void deleteMetadata(@PathParam("rid") String rid) throws Exception;
    @DELETE
    @Path("/metadata/uid/{uid}")
    public void deleteMetadataByUID(@PathParam("uid") String uid) throws Exception;
    @DELETE
    @Path("/metadata/{uid}/{version}")
    public void deleteMetadataByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
    @PUT
    @Path("/metadata/links")
    public void restoreLinks() throws Exception;


    @GET
    @Path("/dsd/{rid}")
    public Object getDsd(@PathParam("rid") String rid) throws Exception;
    @PUT
    @Path("/dsd")
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> DSD updateDsd(T metadata) throws Exception;
    @PATCH
    @Path("/dsd")
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> DSD appendDsd(T metadata) throws Exception;


    @GET
    @Path("/data/rid/{rid}")
    public Object getData(@PathParam("rid") String rid) throws Exception;
    @GET
    @Path("/data/uid/{uid}")
    public Object getDataByUID(@PathParam("uid") String uid) throws Exception;
    @GET
    @Path("/data/{uid}/{version}")
    public Object getDataByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
    @DELETE
    @Path("/data/rid/{rid}")
    public void deleteData(@PathParam("rid") String rid) throws Exception;
    @DELETE
    @Path("/data/uid/{uid}")
    public void deleteDataByUID(@PathParam("uid") String uid) throws Exception;
    @DELETE
    @Path("/data/{uid}/{version}")
    public void deleteDataByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
}
