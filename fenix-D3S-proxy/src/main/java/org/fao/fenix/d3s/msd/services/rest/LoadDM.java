package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.templates.canc.dm.DM;
import org.fao.fenix.d3s.server.services.rest.Service;

@Path("msd/dm")
public class LoadDM extends Service implements org.fao.fenix.d3s.msd.services.spi.LoadDM {
    @Context HttpServletRequest request;

	@Override
	public DM getDatasetMetadata(String uid, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.LoadDM.class).getDatasetMetadata(uid, all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(Boolean all) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.LoadDM.class).getDatasetMetadata(all);
	}

    @Override
    public Collection<DM> getDatasetMetadataLike(String uid, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.LoadDM.class).getDatasetMetadataLike(uid, all);
    }

    @Override
    public Collection<DM> getDatasetMetadata(String[] uids, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.LoadDM.class).getDatasetMetadata(uids, all);
    }
    @Override
    public Collection<String> getDatasetMetadataEcho(String[] uids) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.LoadDM.class).getDatasetMetadataEcho(uids);
    }

    @Override
    public Object getMetadataStructure(String uid, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.d3s.msd.services.spi.LoadDM.class).getMetadataStructure(uid, all);
    }


}
