package org.fao.fenix.backup.services.rest;

import org.fao.fenix.backup.dto.BackupOperation;
import org.fao.fenix.backup.services.impl.ManageBackup;
import org.fao.fenix.server.tools.spring.SpringContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("backup")
public class BackupService implements org.fao.fenix.backup.services.spi.BackupService {
    @Context HttpServletRequest request;

    @Override
    public Integer saveMsdBackup() throws Exception {
        return SpringContext.getBean(ManageBackup.class).storeMsdBackup();
    }
    @Override
    public void restoreMsdBackup(Integer revision) throws Exception {
        SpringContext.getBean(ManageBackup.class).restoreMsdBackup(revision,true);
    }
    @Override
    public void restoreMsdDataBackup(Integer revision) throws Exception {
        SpringContext.getBean(ManageBackup.class).restoreMsdBackup(revision,false);
    }
    @Override
    public BackupOperation statusMsdBackup() throws Exception {
        return SpringContext.getBean(ManageBackup.class).statusMsdBackup();
    }

}
