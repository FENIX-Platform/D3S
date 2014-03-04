package org.fao.fenix.d3s.backup.dao;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import org.fao.fenix.d3s.backup.dto.BackupOperation;
import org.fao.fenix.d3s.backup.dto.BackupStatus;
import org.fao.fenix.d3s.backup.services.impl.BackupRegistry;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
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
