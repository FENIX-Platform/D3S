package org.fao.fenix.msd.services.rest;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.server.services.rest.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Collection;

public class StoreDM extends Service implements org.fao.fenix.msd.services.spi.StoreDM {

    @Override
    public Response newDatasetMetadata(HttpServletRequest request, DM dm) {
        return defaultCall(request, String.class, dm);
    }

    @Override
    public Response indexDatasetMetadata(HttpServletRequest request, String uid) {
        return defaultCall(request, null, uid);
    }

    @Override
    public Response indexDatasetsRebuild(@Context HttpServletRequest request) {
        return defaultCall(request, null);
    }

    @Override
    public Response updateDatasetMetadata(HttpServletRequest request, DM dm) {
        return defaultCall(request, null, dm);
    }

    @Override
    public Response appendDatasetMetadata(HttpServletRequest request, DM dm) {
        return defaultCall(request, null, dm);
    }

    @Override
    public Response deleteDatasetMetadata(HttpServletRequest request, String uid) {
        return defaultCall(request, null, uid);
    }

    @Override
    public Response newMetadataStructure(HttpServletRequest request, DMMeta mm) {
        return defaultCall(request, String.class, mm);
    }

    @Override
    public Response updateMetadataStructure(HttpServletRequest request, DMMeta mm) {
        return defaultCall(request, null, mm);
    }

    @Override
    public Response appendMetadataStructure(HttpServletRequest request, DMMeta mm) {
        return defaultCall(request, null, mm);
    }

    @Override
    public Response deleteMetadataStructure(HttpServletRequest request, String uid) {
        return defaultCall(request, null, uid);
    }

    @Override
    public Response addCategoriesToDataset(HttpServletRequest request, String uid, Collection<Code> listOfCodes) {
        return defaultCall(request, null, uid, listOfCodes);
    }
}
