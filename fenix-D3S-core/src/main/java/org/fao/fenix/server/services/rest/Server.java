package org.fao.fenix.server.services.rest;

import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.cache.impl.RamCache;
import org.fao.fenix.msd.dao.Cleaner;
import org.fao.fenix.msd.dao.dm.DMIndexStore;
import org.fao.fenix.server.dto.OrientStatus;
import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.services.impl.AsynchShutdown;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
public class Server implements org.fao.fenix.server.services.spi.Server {

    @Override
	public Response createMetadataIndex() throws Exception {
		try {
            SpringContext.getBean(DMIndexStore.class).createDynamicIndexStructure();
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

    @Override
	public Response rebuildMetadataIndex() throws Exception {
		try {
            SpringContext.getBean(DMIndexStore.class).rebuildIndexes();
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

    @Override
	public Response removeMetadataIndex() throws Exception {
		try {
            SpringContext.getBean(DMIndexStore.class).removeIndexes();
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

    @Override
	public Response startupSequence() throws Exception {
		try {
			MainController.startupModules();
            MainController.startupOperations();
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

    @Override
	public Response stopServer() {
		try {
			SpringContext.getBean(AsynchShutdown.class).start();
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

    @Override
	public Map<String,String> serverInitParameters() throws Exception {
		return MainController.getInitParameters().toMap();
	}

    @Override
	public void updateServerInitParameters(Map<String,String> parameters) throws Exception {
		MainController.setInitParameters(parameters);
	}

    @Override
	public OrientStatus orientStatus() throws Exception {
		return OrientServer.getsStatus();
	}

    @Override
	public OrientStatus startOrient() throws Exception {
		OrientServer.startServer();
		return OrientServer.getsStatus();
	}

    @Override
	public void stopOrient() throws Exception {
		OrientServer.stopServer();
	}

    @Override
	public Response deleteMsdData() {
		try {
			SpringContext.getBean(Cleaner.class).cleanALL();
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	

}
