package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.d3s.msd.dto.dm.DM;
import org.fao.fenix.d3s.msd.services.impl.Load;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

@Path("msd/dm")
public class LoadDM implements org.fao.fenix.d3s.msd.services.spi.LoadDM {
    @Context HttpServletRequest request;

	@Override
	public DM getDatasetMetadata(String uid, Boolean all) throws Exception {
		return SpringContext.getBean(Load.class).getDatasetMetadata(uid,all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadata(all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(String[] uids, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadata(uids,all);
	}
	@Override
	public Collection<String> getDatasetMetadataEcho(String[] uids) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadataEcho(uids);
	}

	@Override
	public Collection<DM> getDatasetMetadataLike(String uid, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadataLike(uid.replace('*','%'),all);
	}

    @Override
    public Object getMetadataStructure(String uid, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getMetadataStructure(uid,all);
    }


}
