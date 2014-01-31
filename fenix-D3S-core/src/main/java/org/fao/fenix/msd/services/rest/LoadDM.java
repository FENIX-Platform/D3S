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
	public Response getDatasetMetadata(HttpServletRequest request, String uid, String format, Boolean all) {
		try {
			Object datasetValue = SpringContext.getBean(Load.class).getDatasetMetadata(uid,format,all);
            //Object datasetValue = loadBusiness.getDatasetMetadata(uid,format,all);
			return datasetValue!=null ? Response.ok(datasetValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getDatasetMetadata(HttpServletRequest request, Boolean all) {
		try {
			Collection<DM> datasetValue = SpringContext.getBean(Load.class).getDatasetMetadata(all);
			return datasetValue!=null && datasetValue.size()>0 ? Response.ok(datasetValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getDatasetMetadata(HttpServletRequest request, String[] uids, Boolean all) {
		try {
			Collection<DM> datasetValue = SpringContext.getBean(Load.class).getDatasetMetadata(uids,all);
			return datasetValue!=null && datasetValue.size()>0 ? Response.ok(datasetValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getDatasetMetadataEcho(HttpServletRequest request, String[] uids) {
		try {
			Collection<String> datasetValue = SpringContext.getBean(Load.class).getDatasetMetadataEcho(uids);
			return datasetValue!=null && datasetValue.size()>0 ? Response.ok(datasetValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	@Override
	public Response getDatasetMetadataLike(HttpServletRequest request, String uid, Boolean all) {
		try {
			Collection<DM> datasetValue = SpringContext.getBean(Load.class).getDatasetMetadataLike(uid.replace('*','%'),all);
			return datasetValue!=null && datasetValue.size()>0 ? Response.ok(datasetValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

    @Override
    public Response getMetadataStructure(HttpServletRequest request, String uid, Boolean all) {
        try {
            Object metadataStructure = SpringContext.getBean(Load.class).getMetadataStructure(uid,all);
            return metadataStructure!=null? Response.ok(metadataStructure).build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


}
