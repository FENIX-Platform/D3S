package org.fao.fenix.msd.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.msd.services.impl.Delete;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/dsd")
public class StoreDSD implements org.fao.fenix.msd.services.spi.StoreDSD {
	//dimension
	@Override
	public Response newDimension(@Context HttpServletRequest request, DSDDimension dimension) {
		try {
			SpringContext.getBean(Store.class).newDimension(dimension);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response updateDimension(@Context HttpServletRequest request, DSDDimension dimension) {
		try {
			int count =	SpringContext.getBean(Store.class).updateDimension(dimension);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteDimension(@Context HttpServletRequest request, @PathParam("name") String name) {
		try {
			int count =	SpringContext.getBean(Delete.class).deleteDimension(name);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//context system
	@Override
	public Response newContextSystem(@Context HttpServletRequest request, DSDContextSystem context) {
		try {
			SpringContext.getBean(Store.class).newContextSystem(context);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteContextSystem(@Context HttpServletRequest request, @PathParam("name") String name) {
		try {
			int count =	SpringContext.getBean(Delete.class).deleteContextSystem(name);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	//column
	@Override
	public Response updateColumn(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, DSDColumn column) {
		try {
			int count =	SpringContext.getBean(Store.class).updateColumn(uid, column);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	
}
