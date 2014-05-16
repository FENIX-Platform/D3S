package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.d3s.msd.services.impl.Load;

@Path("msd/dm")
public class LoadDM implements org.fao.fenix.d3s.msd.services.spi.LoadDM {
    @Context HttpServletRequest request;
    @Inject private Load load;

	@Override
	public DM getDatasetMetadata(String uid, Boolean all) throws Exception {
        return load.getDatasetMetadata(uid,all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(Boolean all) throws Exception {
        return load.getDatasetMetadata(all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(String[] uids, Boolean all) throws Exception {
        return load.getDatasetMetadata(uids,all);
	}
	@Override
	public Collection<String> getDatasetMetadataEcho(String[] uids) throws Exception {
        return load.getDatasetMetadataEcho(uids);
	}

	@Override
	public Collection<DM> getDatasetMetadataLike(String uid, Boolean all) throws Exception {
        return load.getDatasetMetadataLike(uid.replace('*','%'),all);
	}

    @Override
    public Object getMetadataStructure(String uid, Boolean all) throws Exception {
        return load.getMetadataStructure(uid,all);
    }


}
