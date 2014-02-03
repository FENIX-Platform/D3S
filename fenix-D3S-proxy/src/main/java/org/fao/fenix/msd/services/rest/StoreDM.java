package org.fao.fenix.msd.services.rest;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.server.services.rest.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import java.util.Collection;

@Path("msd/dm")
public class StoreDM extends Service implements org.fao.fenix.msd.services.spi.StoreDM {

    @Override
    public String newDatasetMetadata(HttpServletRequest request, DM dm) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).newDatasetMetadata(request, dm);
    }

    @Override
    public void indexDatasetMetadata(HttpServletRequest request, String uid) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).indexDatasetMetadata(request, uid);
    }

    @Override
    public void indexDatasetsRebuild(HttpServletRequest request) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).indexDatasetsRebuild(request);
    }

    @Override
    public void updateDatasetMetadata(HttpServletRequest request, DM dm) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).updateDatasetMetadata(request, dm);
    }

    @Override
    public void appendDatasetMetadata(HttpServletRequest request, DM dm) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).appendDatasetMetadata(request, dm);
    }

    @Override
    public void deleteDatasetMetadata(HttpServletRequest request, String uid) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).deleteDatasetMetadata(request, uid);
    }

    @Override
    public String newMetadataStructure(HttpServletRequest request, DMMeta mm) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).newMetadataStructure(request, mm);
    }

    @Override
    public void updateMetadataStructure(HttpServletRequest request, DMMeta mm) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).updateMetadataStructure(request, mm);
    }

    @Override
    public void appendMetadataStructure(HttpServletRequest request, DMMeta mm) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).appendMetadataStructure(request, mm);
    }

    @Override
    public void deleteMetadataStructure(HttpServletRequest request, String uid) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).deleteMetadataStructure(request, uid);
    }

    @Override
    public void addCategoriesToDataset(HttpServletRequest request, String uid, Collection<Code> listOfCodes) throws Exception {
        getProxy(org.fao.fenix.msd.services.spi.StoreDM.class).addCategoriesToDataset(request, uid, listOfCodes);
    }
}
