package org.fao.fenix.d3s.server.services.spi;

import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.fao.fenix.d3s.server.dto.OrientStatus;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface Server {

    @POST
    @Path("metadata/index")
    public void createMetadataIndex() throws Exception;
    @PUT
    @Path("metadata/index")
    public void rebuildMetadataIndex() throws Exception;
    @DELETE
    @Path("metadata/index")
    public void removeMetadataIndex() throws Exception;
    @POST
    public void startupSequence() throws Exception;

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
    public void deleteMsdData() throws Exception;

}
