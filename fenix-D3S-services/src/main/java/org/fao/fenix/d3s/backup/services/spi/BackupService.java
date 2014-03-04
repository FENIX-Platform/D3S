package org.fao.fenix.d3s.backup.services.spi;

import org.fao.fenix.d3s.backup.dto.BackupOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
