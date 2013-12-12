package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.server.services.rest.Service;

public class LoadDM extends Service implements org.fao.fenix.msd.services.spi.LoadDM {

	@Override
	public Response getDatasetMetadata(HttpServletRequest request, String uid, String format, Boolean all) {
        return defaultCall(request, DM.class, uid, format, all);
	}
	@Override
	public Response getDatasetMetadata(HttpServletRequest request, Boolean all) {
        return defaultCall(request, Collection.class, all);
	}

    @Override
    public Response getDatasetMetadataLike(HttpServletRequest request, String uid, Boolean all) {
        return defaultCall(request, DM.class, uid, all);
    }

    @Override
    public Response getDatasetMetadata(HttpServletRequest request, String[] uids, Boolean all) {
        return defaultCall(request, Collection.class, uids, all);
    }
    @Override
    public Response getDatasetMetadataEcho(HttpServletRequest request, String[] uids) {
        return defaultCall(request, Collection.class, uids);
    }

    @Override
    public Response getMetadataStructure(HttpServletRequest request, String uid, Boolean all) {
        return defaultCall(request, DMMeta.class, uid, all);
    }


}
