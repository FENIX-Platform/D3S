package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.msd.services.impl.Delete;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/dm")
public class StoreDM implements org.fao.fenix.msd.services.spi.StoreDM {
    @Context HttpServletRequest request;

    //dataset
	@Override
	public String newDatasetMetadata(DM dm) throws Exception {
		return SpringContext.getBean(Store.class).newDatasetMetadata(dm);
	}
	@Override
	public void updateDatasetMetadata(DM dm) throws Exception {
		if (SpringContext.getBean(Store.class).updateDatasetMetadata(dm,false)<=0)
            throw new NoContentException("");
	}
	@Override
	public void appendDatasetMetadata(DM dm) throws Exception {
        if (SpringContext.getBean(Store.class).updateDatasetMetadata(dm,true)<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteDatasetMetadata(String uid) throws Exception {
        if (SpringContext.getBean(Delete.class).deleteDatasetMetadata(uid)<=0)
            throw new NoContentException("");
	}
	@Override
	public void indexDatasetMetadata(String uid) throws Exception {
        if (SpringContext.getBean(Store.class).datasetIndex(uid)<=0)
            throw new NoContentException("");
	}
    @Override
    public void indexDatasetsRebuild() throws Exception {
        if (SpringContext.getBean(Store.class).datasetIndex(null)<=0)
            throw new NoContentException("");
    }


    //structure
    @Override
    public String newMetadataStructure(DMMeta mm) throws Exception {
        return SpringContext.getBean(Store.class).newMetadataStructure(mm);
    }

    @Override
    public void updateMetadataStructure(DMMeta mm) throws Exception {
        if (SpringContext.getBean(Store.class).updateMetadataStructure(mm,false)<=0)
            throw new NoContentException("");
    }

    @Override
    public void appendMetadataStructure(DMMeta mm) throws Exception {
        if (SpringContext.getBean(Store.class).updateMetadataStructure(mm,true)<=0)
            throw new NoContentException("");
    }

    @Override
    public void deleteMetadataStructure(String uid) throws Exception {
        if (SpringContext.getBean(Delete.class).deleteMetadataStructure(uid)<=0)
            throw new NoContentException("");
    }


    //Associate a dataset to one or more categories
	@Override
	public void addCategoriesToDataset(String uid, Collection<Code> listOfCodes) throws Exception {
        if (SpringContext.getBean(Store.class).addCategoriesToDataset(uid, listOfCodes)<=0)
            throw new NoContentException("");
	}
}
