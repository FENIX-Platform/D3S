package org.fao.fenix.d3s.msd.services.spi;

import java.util.Collection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.d3s.msd.dto.dm.DM;
import org.fao.fenix.d3s.msd.dto.dm.DMMeta;
import org.fao.fenix.d3s.msd.dto.cl.Code;

@Produces
@Consumes(MediaType.APPLICATION_JSON)
public interface StoreDM {

	//dataset
	@POST
	@Path("dataset")
	public String newDatasetMetadata(DM dm) throws Exception;
    @PUT
    @Path("index/{datasetUID}")
    @Consumes()
    public void indexDatasetMetadata(@PathParam("datasetUID") String uid) throws Exception;
    @PUT
    @Path("index/rebuild")
    @Consumes()
    public void indexDatasetsRebuild() throws Exception;
    @PUT
    @Path("dataset")
    public void updateDatasetMetadata(DM dm) throws Exception;
	@PUT
	@Path("dataset/append")
	public void appendDatasetMetadata(DM dm) throws Exception;
	@DELETE
	@Consumes()
	@Path("dataset/{datasetUID}")
	public void deleteDatasetMetadata(@PathParam("datasetUID") String uid) throws Exception;
	

	//structure
	@POST
	@Path("format")
	public String newMetadataStructure(DMMeta mm) throws Exception;
	@PUT
	@Path("format")
	public void updateMetadataStructure(DMMeta mm) throws Exception;
	@PUT
	@Path("format/append")
	public void appendMetadataStructure(DMMeta mm) throws Exception;
	@DELETE
	@Consumes()
	@Path("format/{metadataUID}")
	public void deleteMetadataStructure(@PathParam("metadataUID") String uid) throws Exception;


	//Associate a dataset to one or more categories
	@PUT
	@Path("category/{datasetUID}")
	public void addCategoriesToDataset(@PathParam("datasetUID") String uid, Collection<Code> listOfCodes) throws Exception;
}
