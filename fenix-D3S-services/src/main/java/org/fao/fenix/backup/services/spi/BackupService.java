package org.fao.fenix.backup.services.spi;

import org.fao.fenix.msd.dto.dm.DMMeta;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface BackupService {

    @POST
    @Path("msd")
    public Response saveMsdBackup(@Context HttpServletRequest request);

    @PUT
    @Path("msd/{revision}")
    public Response restoreMsdBackup(@Context HttpServletRequest request, @PathParam("revision") Integer revision);

    @PUT
    @Path("msd/data/{revision}")
    public Response restoreMsdDataBackup(@Context HttpServletRequest request, @PathParam("revision") Integer revision);

    @GET
    @Path("msd")
    public Response statusMsdBackup(@Context HttpServletRequest request);

    @GET
    @Path("{database}/{revision}")
    public Response restoreBackup(@Context HttpServletRequest request, @PathParam("database") String database, @PathParam("revision") String revision);

    @DELETE
    @Path("{database}/{revision}")
    public Response removeBackup(@Context HttpServletRequest request, @PathParam("database") String database, @PathParam("revision") String revision);


}
