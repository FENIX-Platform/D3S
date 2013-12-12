package org.fao.fenix.backup.dao;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.db.record.ODatabaseRecord;
import org.fao.fenix.backup.dto.BackupMeta;
import org.fao.fenix.backup.dto.BackupOperation;
import org.fao.fenix.backup.dto.BackupStatus;
import org.fao.fenix.backup.services.impl.BackupRegistry;
import org.fao.fenix.server.tools.orient.OrientDatabase;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.fao.fenix.server.utils.CompletenessIterator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Backup implements Runnable  {
    @Autowired protected BackupRegistry registry;
    @Autowired protected BackupIO io;

    private OrientDatabase database;
    protected OGraphDatabase currentDatabase;

    private static final Map<OrientDatabase,BackupOperation> statusMap = new HashMap<OrientDatabase, BackupOperation>();

    public BackupOperation getStatus(OrientDatabase database) {
        return statusMap.get(database);
    }
    protected void setStatus(OrientDatabase database, BackupOperation operation) {
        statusMap.put(database,operation);
    }

    protected void start(OrientDatabase database) {
        this.database = database;
        currentDatabase = OrientServer.getDatabase(database);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            ODatabaseRecordThreadLocal.INSTANCE.set(currentDatabase);

            execute(registry, database);

            BackupOperation status = statusMap.get(database);
            switch (status.getStatus()) {
                case store: status.setStatus(BackupStatus.stored); break;
                case restore: status.setStatus(BackupStatus.restored); break;
            }
        } catch (Exception ex) {
            BackupOperation status = statusMap.get(database);
            status.setErrorMessage(ex.getMessage());
            switch (status.getStatus()) {
                case store: status.setStatus(BackupStatus.storeError); break;
                case restore: status.setStatus(BackupStatus.restoreError); break;
            }
        }
    }

    protected abstract void execute(BackupRegistry registry, OrientDatabase database) throws Exception;

}
