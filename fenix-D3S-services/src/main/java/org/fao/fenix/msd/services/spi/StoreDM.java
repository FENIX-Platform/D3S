package org.fao.fenix.msd.services.spi;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreDM {

	//dataset
	@POST
	@Path("dataset")
	public String newDatasetMetadata(@Context HttpServletRequest request, DM dm) throws Exception;
    @PUT
    @Path("index/{datasetUID}")
    @Consumes()
    public void indexDatasetMetadata(@Context HttpServletRequest request, @PathParam("datasetUID") String uid) throws Exception;
    @PUT
    @Path("index/rebuild")
    @Consumes()
    public void indexDatasetsRebuild(@Context HttpServletRequest request) throws Exception;
    @PUT
    @Path("dataset")
    public void updateDatasetMetadata(@Context HttpServletRequest request, DM dm) throws Exception;
	@PUT
	@Path("dataset/append")
	public void appendDatasetMetadata(@Context HttpServletRequest request, DM dm) throws Exception;
	@DELETE
	@Consumes()
	@Path("dataset/{datasetUID}")
	public void deleteDatasetMetadata(@Context HttpServletRequest request, @PathParam("datasetUID") String uid) throws Exception;
	

	//structure
	@POST
	@Path("format")
	public String newMetadataStructure(@Context HttpServletRequest request, DMMeta mm) throws Exception;
	@PUT
	@Path("format")
	public void updateMetadataStructure(@Context HttpServletRequest request, DMMeta mm) throws Exception;
	@PUT
	@Path("format/append")
	public void appendMetadataStructure(@Context HttpServletRequest request, DMMeta mm) throws Exception;
	@DELETE
	@Consumes()
	@Path("format/{metadataUID}")
	public void deleteMetadataStructure(@Context HttpServletRequest request, @PathParam("metadataUID") String uid) throws Exception;


	//Associate a dataset to one or more categories
	@PUT
	@Path("category/{datasetUID}")
	public void addCategoriesToDataset(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, Collection<Code> listOfCodes) throws Exception;
}
