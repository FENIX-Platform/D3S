package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.services.impl.Load;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Path("msd/dm")
public class LoadDM implements org.fao.fenix.msd.services.spi.LoadDM {

	@Override
	public DM getDatasetMetadata(HttpServletRequest request, String uid, Boolean all) throws Exception {
		return SpringContext.getBean(Load.class).getDatasetMetadata(uid,all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(HttpServletRequest request, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadata(all);
	}
	@Override
	public Collection<DM> getDatasetMetadata(HttpServletRequest request, String[] uids, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadata(uids,all);
	}
	@Override
	public Collection<String> getDatasetMetadataEcho(HttpServletRequest request, String[] uids) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadataEcho(uids);
	}

	@Override
	public Collection<DM> getDatasetMetadataLike(HttpServletRequest request, String uid, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getDatasetMetadataLike(uid.replace('*','%'),all);
	}

    @Override
    public Object getMetadataStructure(HttpServletRequest request, String uid, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getMetadataStructure(uid,all);
    }


}
