package org.fao.fenix.d3s.backup.dao;

import org.fao.fenix.d3s.backup.dto.BackupMeta;
import org.fao.fenix.d3s.backup.dto.BackupOperation;
import org.fao.fenix.d3s.backup.dto.BackupStatus;
import org.fao.fenix.d3s.backup.services.impl.BackupRegistry;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.commons.utils.CompletenessIterator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Component
public class StoreBackup extends Backup {
    private CompletenessIterator<?>[] data;


    public synchronized int storeNextRevision(OrientDatabase database, long size, CompletenessIterator<?> ... data) throws Exception {
        this.data = data;

        String databaseName = database.getDatabaseName();
        BackupOperation operation = getStatus(database);
        if (operation==null || (operation.getStatus()!=BackupStatus.store && operation.getStatus()!=BackupStatus.restore)) {
            setStatus(database, new BackupOperation(databaseName,registry.currentRevision(databaseName)+1,BackupStatus.store,data,size));
            start(database);
            return registry.currentRevision(databaseName)+1;
        } else
            return registry.currentRevision(databaseName);
    }


    @Override
    protected void execute(BackupRegistry registry, OrientDatabase database) throws Exception {
        String databaseName = database.getDatabaseName();

        BackupMeta meta = new BackupMeta(databaseName,registry.currentRevision(databaseName)+1,registry.getDDL(databaseName),new Date());
        File file = registry.getFile(meta);
        io.setOut(new FileOutputStream(file, false));

        io.write(meta);
        for (Iterator<?> dataSection : data)
            io.write(dataSection);
        io.close();

        registry.addBackup(meta);
    }
}
