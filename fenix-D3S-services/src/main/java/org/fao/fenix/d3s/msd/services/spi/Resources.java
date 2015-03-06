package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.data.MetadataList;
import org.fao.fenix.commons.msd.dto.data.ReplicationFilter;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.templates.identification.DSD;
import org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

public interface Resources {

    //MASSIVE METADATA
    @POST
    @Path("/massive/load")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<Object> getMetadata(StandardFilter filter, @QueryParam("logic") String businessName, @QueryParam("full") @DefaultValue("true") boolean full, @QueryParam("dsd") @DefaultValue("true") boolean dsd, @QueryParam("export") @DefaultValue("true") boolean export) throws Exception;
    @POST
    @Path("/massive")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<MeIdentification> insertMetadata(MetadataList metadata) throws Exception;
    @PUT
    @Path("/massive")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<MeIdentification> updateMetadata(MetadataList metadata) throws Exception;
    @PATCH
    @Path("/massive")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<MeIdentification> appendMetadata(MetadataList metadata) throws Exception;
    @DELETE
    @Path("/massive")
    public Integer deleteMetadata(StandardFilter filter, @QueryParam("logic") String businessName) throws Exception;

    @PATCH
    @Path("/replication")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> Collection<MeIdentification> appendReplicationMetadata(ReplicationFilter<T> replicationFilter, @QueryParam("logic") String businessName) throws Exception;



    //RESOURCES

    @GET
    @Path("/rid/{rid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public ResourceProxy getResource(@PathParam("rid") String rid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd, @QueryParam("export") @DefaultValue("false") boolean export) throws Exception;
    @GET
    @Path("/uid/{uid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public ResourceProxy getResourceByUID(@PathParam("uid") String uid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd, @QueryParam("export") @DefaultValue("false") boolean export) throws Exception;
    @GET
    @Path("/{uid}/{version}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public ResourceProxy getResourceByUID(@PathParam("uid") String uid, @PathParam("version") String version, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd, @QueryParam("export") @DefaultValue("false") boolean export) throws Exception;
    @POST
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
    public MeIdentification insertResource(Resource resource) throws Exception;
    @PUT
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
    public MeIdentification updateResource(Resource resource) throws Exception;
    @PATCH
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes({MediaType.APPLICATION_JSON, "application/csv"})
    public MeIdentification appendResource(Resource resource) throws Exception;
    @DELETE
    @Path("/rid/{rid}")
    public String deleteResource(@PathParam("rid") String rid) throws Exception;
    @DELETE
    @Path("/uid/{uid}")
    public String deleteResourceByUID(@PathParam("uid") String uid) throws Exception;
    @DELETE
    @Path("/{uid}/{version}")
    public String deleteResourceByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;


    @GET
    @Path("/metadata/rid/{rid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getMetadata(@PathParam("rid") String rid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd, @QueryParam("export") @DefaultValue("false") boolean export) throws Exception;
    @GET
    @Path("/metadata/uid/{uid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getMetadataByUID(@PathParam("uid") String uid, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd, @QueryParam("export") @DefaultValue("false") boolean export) throws Exception;
    @GET
    @Path("/metadata/{uid}/{version}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getMetadataByUID(@PathParam("uid") String uid, @PathParam("version") String version, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd, @QueryParam("export") @DefaultValue("false") boolean export) throws Exception;
    @POST
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification insertMetadata(T metadata) throws Exception;
    @PUT
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification updateMetadata(T metadata) throws Exception;
    @PATCH
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.MeIdentification> MeIdentification appendMetadata(T metadata) throws Exception;
    @DELETE
    @Path("/metadata/rid/{rid}")
    public String deleteMetadata(@PathParam("rid") String rid) throws Exception;
    @DELETE
    @Path("/metadata/uid/{uid}")
    public String deleteMetadataByUID(@PathParam("uid") String uid) throws Exception;
    @DELETE
    @Path("/metadata/{uid}/{version}")
    public String deleteMetadataByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
    @PUT
    @Path("/metadata/links")
    public void restoreLinks() throws Exception;


    @GET
    @Path("/dsd/{rid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getDsd(@PathParam("rid") String rid) throws Exception;
    @PUT
    @Path("/dsd")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> DSD updateDsd(T metadata) throws Exception;
    @PATCH
    @Path("/dsd")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public <T extends org.fao.fenix.commons.msd.dto.full.DSD> DSD appendDsd(T metadata) throws Exception;
    @DELETE
    @Path("/dsd/{rid}")
    public void deleteDsd(@PathParam("rid") String rid) throws Exception;


    @GET
    @Path("/data/rid/{rid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getData(@PathParam("rid") String rid) throws Exception;
    @GET
    @Path("/data/uid/{uid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getDataByUID(@PathParam("uid") String uid) throws Exception;
    @GET
    @Path("/data/{uid}/{version}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getDataByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;
/*    @PUT
    @Path("/data/rid/{rid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public MeIdentification updateData(@PathParam("rid") String rid, Collection data) throws Exception;
    @PUT
    @Path("/data/uid/{uid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public MeIdentification updateDataByUid(@PathParam("uid") String uid, Collection data) throws Exception;
    @PUT
    @Path("/data/{uid}/{version}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public MeIdentification updateDataByUid(@PathParam("uid") String uid, @PathParam("version") String version, Collection data) throws Exception;
    @PATCH
    @Path("/data/rid/{rid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public MeIdentification appendData(@PathParam("rid") String rid, Collection data) throws Exception;
    @PATCH
    @Path("/data/uid/{uid}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public MeIdentification appendDataByUid(@PathParam("uid") String uid, Collection data) throws Exception;
    @PATCH
    @Path("/data/{uid}/{version}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public MeIdentification appendDataByUid(@PathParam("uid") String uid, @PathParam("version") String version, Collection data) throws Exception;
*/    @DELETE
    @Path("/data/rid/{rid}")
    public String deleteData(@PathParam("rid") String rid) throws Exception;
    @DELETE
    @Path("/data/uid/{uid}")
    public String deleteDataByUID(@PathParam("uid") String uid) throws Exception;
    @DELETE
    @Path("/data/{uid}/{version}")
    public String deleteDataByUID(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception;


    @POST
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection findMetadata(StandardFilter filter, @QueryParam("logic") String businessName, @QueryParam("full") @DefaultValue("false") boolean full, @QueryParam("dsd") @DefaultValue("false") boolean dsd, @QueryParam("export") @DefaultValue("false") boolean export) throws Exception;

}
