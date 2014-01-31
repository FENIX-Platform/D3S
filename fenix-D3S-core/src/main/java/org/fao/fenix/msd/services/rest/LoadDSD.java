package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.msd.services.impl.Load;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/dsd")
public class LoadDSD implements org.fao.fenix.msd.services.spi.LoadDSD {

	@Override
	public Response getDatasources(@Context HttpServletRequest request) {
		try {
			Collection<DSDDatasource> datasetValue = SpringContext.getBean(Load.class).getDatasources();
			return Response.ok(datasetValue).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getDimensions(@Context HttpServletRequest request) {
		try {
			Collection<DSDDimension> datasetValue = SpringContext.getBean(Load.class).getDimensions();
			return Response.ok(datasetValue).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getContextSystems(@Context HttpServletRequest request) {
		try {
			Collection<DSDContextSystem> datasetValue = SpringContext.getBean(Load.class).getContextSystems();
			return Response.ok(datasetValue).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
}
