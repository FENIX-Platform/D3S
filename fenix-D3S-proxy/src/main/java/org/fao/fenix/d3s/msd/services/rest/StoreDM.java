package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.dm.DM;
import org.fao.fenix.d3s.msd.dto.dm.DMMeta;
import org.fao.fenix.d3s.server.services.rest.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.Collection;

@Path("msd/dm")
public class StoreDM extends Service implements org.fao.fenix.d3s.msd.services.spi.StoreDM {
    @Context HttpServletRequest request;

    @Override
    public String newDatasetMetadata(DM dm) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).newDatasetMetadata(dm);
    }

    @Override
    public void indexDatasetMetadata(String uid) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).indexDatasetMetadata(uid);
    }

    @Override
    public void indexDatasetsRebuild() throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).indexDatasetsRebuild();
    }

    @Override
    public void updateDatasetMetadata(DM dm) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).updateDatasetMetadata(dm);
    }

    @Override
    public void appendDatasetMetadata(DM dm) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).appendDatasetMetadata(dm);
    }

    @Override
    public void deleteDatasetMetadata(String uid) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).deleteDatasetMetadata(uid);
    }

    @Override
    public String newMetadataStructure(DMMeta mm) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).newMetadataStructure(mm);
    }

    @Override
    public void updateMetadataStructure(DMMeta mm) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).updateMetadataStructure(mm);
    }

    @Override
    public void appendMetadataStructure(DMMeta mm) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).appendMetadataStructure(mm);
    }

    @Override
    public void deleteMetadataStructure(String uid) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).deleteMetadataStructure(uid);
    }

    @Override
    public void addCategoriesToDataset(String uid, Collection<Code> listOfCodes) throws Exception {
        getProxy(org.fao.fenix.d3s.msd.services.spi.StoreDM.class).addCategoriesToDataset(uid, listOfCodes);
    }
}
