package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.templates.canc.dm.DM;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface LoadDM {

	@GET
	@Path("{datasetUID}")
	public DM getDatasetMetadata(@PathParam("datasetUID") String uid, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
	@GET
	public Collection<DM> getDatasetMetadata(@QueryParam("all") @DefaultValue("false") Boolean all) throws Exception;
    @GET
    @Path("like/{datasetUID}")
    public Collection<DM> getDatasetMetadataLike(@PathParam("datasetUID") String uid, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
    @POST
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<DM> getDatasetMetadata(String[] uids, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
    @POST
    @Path("echo")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<String> getDatasetMetadataEcho(String[] uids) throws Exception;

    @GET
    @Path("format/{metadataUID}")
    public Object getMetadataStructure(@PathParam("metadataUID") String uid, @QueryParam("all") @DefaultValue("false") Boolean all) throws Exception;
}
