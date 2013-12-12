package org.fao.fenix.server.services.spi;

import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.server.dto.OrientStatus;

@Path("server")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface Server {

    @POST
    @Path("metadata/index")
    public Response createMetadataIndex() throws Exception;
    @PUT
    @Path("metadata/index")
    public Response rebuildMetadataIndex() throws Exception;
    @DELETE
    @Path("metadata/index")
    public Response removeMetadataIndex() throws Exception;
    @POST
    public Response startupSequence() throws Exception;
    @DELETE
    public Response stopServer();

    @GET
    @Path("init/parameters")
    public Map<String,String> serverInitParameters() throws Exception;


    @POST
    @Path("init/parameters")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateServerInitParameters(Map<String,String> parameters) throws Exception;

    @GET
    @Path("orient")
    public OrientStatus orientStatus() throws Exception;

    @POST
    @Path("orient")
    public OrientStatus startOrient() throws Exception;

    @DELETE
    @Path("orient")
    public void stopOrient() throws Exception;

    @DELETE
    @Path("database/msd")
    public Response deleteMsdData();

}
