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

    @GET
    @Path("init/parameters")
    public Map<String,String> serverInitParameters() throws Exception;

    @GET
    @Path("orient")
    public OrientStatus orientStatus() throws Exception;

}
