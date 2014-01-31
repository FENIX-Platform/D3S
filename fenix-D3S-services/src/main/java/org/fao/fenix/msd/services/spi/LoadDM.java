package org.fao.fenix.msd.services.spi;

import org.fao.fenix.msd.dto.dm.DMMeta;
import org.jboss.resteasy.annotations.GZIP;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface LoadDM {

	@GET
    @GZIP
	@Path("{datasetUID}")
	public Response getDatasetMetadata(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, @QueryParam("format") @DefaultValue(DMMeta.DEFAULT_FORMAT) String format, @QueryParam("all") @DefaultValue("true") Boolean all);
	@GET
    @GZIP
	public Response getDatasetMetadata(@Context HttpServletRequest request, @QueryParam("all") @DefaultValue("false") Boolean all);
    @GET
    @GZIP
    @Path("like/{datasetUID}")
    public Response getDatasetMetadataLike(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, @QueryParam("all") @DefaultValue("true") Boolean all);
    @POST
    @GZIP
    @Path("list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDatasetMetadata(@Context HttpServletRequest request, String[] uids, @QueryParam("all") @DefaultValue("true") Boolean all);
    @POST
    @GZIP
    @Path("echo")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getDatasetMetadataEcho(@Context HttpServletRequest request, String[] uids);

    @GET
    @GZIP
    @Path("format/{metadataUID}")
    public Response getMetadataStructure(@Context HttpServletRequest request, @PathParam("metadataUID") String uid, @QueryParam("all") @DefaultValue("false") Boolean all);
}
