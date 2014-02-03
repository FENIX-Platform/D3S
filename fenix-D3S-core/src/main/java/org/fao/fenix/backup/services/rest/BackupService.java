package org.fao.fenix.backup.services.rest;

import org.fao.fenix.backup.dto.BackupOperation;
import org.fao.fenix.backup.dto.BackupStatus;
import org.fao.fenix.backup.services.impl.ManageBackup;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("backup")
public class BackupService implements org.fao.fenix.backup.services.spi.BackupService {
    @Override
    public Integer saveMsdBackup(HttpServletRequest request) throws Exception {
        return SpringContext.getBean(ManageBackup.class).storeMsdBackup();
    }
    @Override
    public void restoreMsdBackup(HttpServletRequest request, Integer revision) throws Exception {
        SpringContext.getBean(ManageBackup.class).restoreMsdBackup(revision,true);
    }
    @Override
    public void restoreMsdDataBackup(HttpServletRequest request, Integer revision) throws Exception {
        SpringContext.getBean(ManageBackup.class).restoreMsdBackup(revision,false);
    }
    @Override
    public BackupOperation statusMsdBackup(HttpServletRequest request) throws Exception {
        return SpringContext.getBean(ManageBackup.class).statusMsdBackup();
    }

}
