package org.fao.fenix.backup.services.rest;

import org.fao.fenix.backup.dto.BackupOperation;
import org.fao.fenix.backup.dto.BackupStatus;
import org.fao.fenix.backup.services.impl.ManageBackup;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public class BackupService implements org.fao.fenix.backup.services.spi.BackupService {
    @Override
    public Response saveMsdBackup(HttpServletRequest request) {
        try {
            int revision = SpringContext.getBean(ManageBackup.class).storeMsdBackup();
            return Response.ok(revision).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    @Override
    public Response restoreMsdBackup(HttpServletRequest request, Integer revision) {
        try {
            SpringContext.getBean(ManageBackup.class).restoreMsdBackup(revision,true);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    @Override
    public Response restoreMsdDataBackup(HttpServletRequest request, Integer revision) {
        try {
            SpringContext.getBean(ManageBackup.class).restoreMsdBackup(revision,false);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    @Override
    public Response statusMsdBackup(HttpServletRequest request) {
        try {
            BackupOperation status = SpringContext.getBean(ManageBackup.class).statusMsdBackup();
            return Response.ok(status).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response restoreBackup(HttpServletRequest request, String database, String revision) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Response removeBackup(HttpServletRequest request,String database, String revision) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
