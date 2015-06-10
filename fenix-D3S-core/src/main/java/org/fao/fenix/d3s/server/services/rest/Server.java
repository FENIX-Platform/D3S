package org.fao.fenix.d3s.server.services.rest;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

//import org.fao.fenix.d3s.msd.dao.canc.dm.DMIndexStore;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.fao.fenix.d3s.server.dto.OrientStatus;
import org.fao.fenix.d3s.wds.WDSDaoFactory;


@Path("server")
public class Server {
    @Context HttpServletRequest request;
    @Inject private OrientServer orientServer;
    @Inject private WDSDaoFactory daoFactory;



    @GET
    @Path("/orient")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public OrientStatus orientStatus() throws Exception {
        return orientServer.getStatus();
    }

    @GET
    @Path("/init/parameters")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
	public Map<String,String> serverInitParameters() throws Exception {
		return MainController.getInitParameters().toMap();
	}


    @GET
    @Path("/ds/{id}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Map<String,String> getProeprties(@PathParam("id") String name) throws Exception {
        return name!=null ? daoFactory.getDatasourceProperties(name) : null;
    }



}
