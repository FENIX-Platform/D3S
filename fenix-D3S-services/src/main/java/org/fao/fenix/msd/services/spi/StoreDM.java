package org.fao.fenix.msd.services.spi;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;

@Consumes(MediaType.APPLICATION_JSON)
public interface StoreDM {

	//dataset
	@POST
	@Path("dataset")
	public Response newDatasetMetadata(@Context HttpServletRequest request, DM dm);
    @PUT
    @Path("index/{datasetUID}")
    @Consumes()
    public Response indexDatasetMetadata(@Context HttpServletRequest request, @PathParam("datasetUID") String uid);
    @PUT
    @Path("index/rebuild")
    @Consumes()
    public Response indexDatasetsRebuild(@Context HttpServletRequest request);
    @PUT
    @Path("dataset")
    public Response updateDatasetMetadata(@Context HttpServletRequest request, DM dm);
	@PUT
	@Path("dataset/append")
	public Response appendDatasetMetadata(@Context HttpServletRequest request, DM dm);
	@DELETE
	@Consumes()
	@Path("dataset/{datasetUID}")
	public Response deleteDatasetMetadata(@Context HttpServletRequest request, @PathParam("datasetUID") String uid);
	

	//structure
	@POST
	@Path("format")
	public Response newMetadataStructure(@Context HttpServletRequest request, DMMeta mm);
	@PUT
	@Path("format")
	public Response updateMetadataStructure(@Context HttpServletRequest request, DMMeta mm);
	@PUT
	@Path("format/append")
	public Response appendMetadataStructure(@Context HttpServletRequest request, DMMeta mm);
	@DELETE
	@Consumes()
	@Path("format/{metadataUID}")
	public Response deleteMetadataStructure(@Context HttpServletRequest request, @PathParam("metadataUID") String uid);


	//Associate a dataset to one or more categories
	@PUT
	@Path("category/{datasetUID}")
	public Response addCategoriesToDataset(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, Collection<Code> listOfCodes); 
}
