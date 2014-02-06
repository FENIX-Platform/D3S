package org.fao.fenix.msd.services.spi;

import org.fao.fenix.msd.dto.dm.DM;
import org.jboss.resteasy.annotations.GZIP;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface LoadDM {

	@GET
    @GZIP
	@Path("{datasetUID}")
	public DM getDatasetMetadata(@PathParam("datasetUID") String uid, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
	@GET
    @GZIP
	public Collection<DM> getDatasetMetadata(@QueryParam("all") @DefaultValue("false") Boolean all) throws Exception;
    @GET
    @GZIP
    @Path("like/{datasetUID}")
    public Collection<DM> getDatasetMetadataLike(@PathParam("datasetUID") String uid, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
    @POST
    @GZIP
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<DM> getDatasetMetadata(String[] uids, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
    @POST
    @GZIP
    @Path("echo")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<String> getDatasetMetadataEcho(String[] uids) throws Exception;

    @GET
    @GZIP
    @Path("format/{metadataUID}")
    public Object getMetadataStructure(@PathParam("metadataUID") String uid, @QueryParam("all") @DefaultValue("false") Boolean all) throws Exception;
}
