package org.fao.fenix.d3s.msd.services.rest.canc;

import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.commons.msd.dto.templates.canc.cl.Code;
import org.fao.fenix.commons.msd.dto.templates.canc.dm.DM;
import org.fao.fenix.commons.msd.dto.templates.canc.dm.DMMeta;
import org.fao.fenix.d3s.msd.services.impl.Delete;
import org.fao.fenix.d3s.msd.services.impl.Store;

@Path("msd/dm")
public class StoreDM implements org.fao.fenix.d3s.msd.services.spi.canc.StoreDM {
    @Context HttpServletRequest request;
    @Inject private Store store;
    @Inject private Delete delete;

    //dataset
	@Override
	public String newDatasetMetadata(DM dm) throws Exception {
		return store.newDatasetMetadata(dm);
	}
	@Override
	public void updateDatasetMetadata(DM dm) throws Exception {
		if (store.updateDatasetMetadata(dm,false)<=0)
            throw new NoContentException("");
	}
	@Override
	public void appendDatasetMetadata(DM dm) throws Exception {
        if (store.updateDatasetMetadata(dm,true)<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteDatasetMetadata(String uid) throws Exception {
        if (delete.deleteDatasetMetadata(uid)<=0)
            throw new NoContentException("");
	}
	@Override
	public void indexDatasetMetadata(String uid) throws Exception {
        if (store.datasetIndex(uid)<=0)
            throw new NoContentException("");
	}
    @Override
    public void indexDatasetsRebuild() throws Exception {
        if (store.datasetIndex(null)<=0)
            throw new NoContentException("");
    }


    //structure
    @Override
    public String newMetadataStructure(DMMeta mm) throws Exception {
        return store.newMetadataStructure(mm);
    }

    @Override
    public void updateMetadataStructure(DMMeta mm) throws Exception {
        if (store.updateMetadataStructure(mm,false)<=0)
            throw new NoContentException("");
    }

    @Override
    public void appendMetadataStructure(DMMeta mm) throws Exception {
        if (store.updateMetadataStructure(mm,true)<=0)
            throw new NoContentException("");
    }

    @Override
    public void deleteMetadataStructure(String uid) throws Exception {
        if (delete.deleteMetadataStructure(uid)<=0)
            throw new NoContentException("");
    }


    //Associate a dataset to one or more categories
	@Override
	public void addCategoriesToDataset(String uid, Collection<Code> listOfCodes) throws Exception {
        if (store.addCategoriesToDataset(uid, listOfCodes)<=0)
            throw new NoContentException("");
	}
}
