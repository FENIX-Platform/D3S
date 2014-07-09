package org.fao.fenix.d3s.server.services.rest;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

//import org.fao.fenix.d3s.msd.dao.canc.dm.DMIndexStore;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.fao.fenix.d3s.server.dto.OrientStatus;


@Path("server")
public class Server implements org.fao.fenix.d3s.server.services.spi.Server {
    @Context HttpServletRequest request;
//    @Inject private DMIndexStore dmIndexStore;
    @Inject private OrientServer orientServer;
/*
    @Override
	public void createMetadataIndex() throws Exception {
		dmIndexStore.createDynamicIndexStructure();
	}

    @Override
	public void rebuildMetadataIndex() throws Exception {
		dmIndexStore.rebuildIndexes();
	}

    @Override
	public void removeMetadataIndex() throws Exception {
		dmIndexStore.removeIndexes();
	}
*/
    @Override
    public OrientStatus orientStatus() throws Exception {
        return orientServer.getStatus();
    }

    @Override
	public Map<String,String> serverInitParameters() throws Exception {
		return MainController.getInitParameters().toMap();
	}


}
