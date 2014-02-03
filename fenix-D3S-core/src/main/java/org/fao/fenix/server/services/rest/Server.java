package org.fao.fenix.server.services.rest;

import java.util.Map;

import javax.ws.rs.*;

import org.fao.fenix.msd.dao.Cleaner;
import org.fao.fenix.msd.dao.dm.DMIndexStore;
import org.fao.fenix.server.dto.OrientStatus;
import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.services.impl.AsynchShutdown;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.server.tools.spring.SpringContext;


@Path("server")
public class Server implements org.fao.fenix.server.services.spi.Server {

    @Override
	public void createMetadataIndex() throws Exception {
		SpringContext.getBean(DMIndexStore.class).createDynamicIndexStructure();
	}

    @Override
	public void rebuildMetadataIndex() throws Exception {
		SpringContext.getBean(DMIndexStore.class).rebuildIndexes();
	}

    @Override
	public void removeMetadataIndex() throws Exception {
		SpringContext.getBean(DMIndexStore.class).removeIndexes();
	}

    @Override
	public void startupSequence() throws Exception {
        MainController.startupModules();
        MainController.startupOperations();
	}

    @Override
	public void stopServer() throws Exception {
		SpringContext.getBean(AsynchShutdown.class).start();
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
	public void deleteMsdData() throws Exception {
		SpringContext.getBean(Cleaner.class).cleanALL();
	}
	

}
