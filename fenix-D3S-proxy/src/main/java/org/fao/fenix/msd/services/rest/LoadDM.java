package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.server.services.rest.Service;

@Path("msd/dm")
public class LoadDM extends Service implements org.fao.fenix.msd.services.spi.LoadDM {

	@Override
	public DM getDatasetMetadata(HttpServletRequest request, String uid, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadDM.class).getDatasetMetadata(request, uid, all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(HttpServletRequest request, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadDM.class).getDatasetMetadata(request, all);
	}

    @Override
    public Collection<DM> getDatasetMetadataLike(HttpServletRequest request, String uid, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadDM.class).getDatasetMetadataLike(request, uid, all);
    }

    @Override
    public Collection<DM> getDatasetMetadata(HttpServletRequest request, String[] uids, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadDM.class).getDatasetMetadata(request, uids, all);
    }
    @Override
    public Collection<String> getDatasetMetadataEcho(HttpServletRequest request, String[] uids) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadDM.class).getDatasetMetadataEcho(request, uids);
    }

    @Override
    public Object getMetadataStructure(HttpServletRequest request, String uid, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadDM.class).getMetadataStructure(request, uid, all);
    }


}
