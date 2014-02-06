package org.fao.fenix.backup.services.spi;

import org.fao.fenix.backup.dto.BackupOperation;
import org.fao.fenix.msd.dto.dm.DMMeta;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface BackupService {

    @POST
    @Path("msd")
    public Integer saveMsdBackup() throws Exception;

    @PUT
    @Path("msd/{revision}")
    public void restoreMsdBackup(@PathParam("revision") Integer revision) throws Exception;

    @PUT
    @Path("msd/data/{revision}")
    public void restoreMsdDataBackup(@PathParam("revision") Integer revision) throws Exception;

    @GET
    @Path("msd")
    public BackupOperation statusMsdBackup() throws Exception;



}
