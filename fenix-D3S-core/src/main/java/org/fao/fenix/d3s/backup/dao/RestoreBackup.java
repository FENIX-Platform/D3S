package org.fao.fenix.d3s.backup.dao;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import org.fao.fenix.d3s.backup.dto.BackupMeta;
import org.fao.fenix.d3s.backup.dto.BackupOperation;
import org.fao.fenix.d3s.backup.dto.BackupStatus;
import org.fao.fenix.d3s.backup.dto.BackupUnit;
import org.fao.fenix.d3s.backup.services.impl.BackupRegistry;
import org.fao.fenix.d3s.msd.dao.dm.DMIndexStore;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.fao.fenix.d3s.server.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

@Component
public class RestoreBackup extends Backup {
    @Autowired
    DMIndexStore msdIndexDao;

    private Iterator<BackupUnit> dataIterator;
    private Integer selectedRevision;
    private RestoreBackupConsumer consumer;


    public synchronized void restoreDatabase(OrientDatabase database, Integer revision, RestoreBackupConsumer consumer, boolean structure) throws Exception {
        String databaseName = database.getDatabaseName();
        this.consumer = consumer;
        this.selectedRevision = revision==null ? registry.currentRevision(databaseName) : revision;

        BackupOperation operation = getStatus(database);
        if (operation==null || (operation.getStatus()!= BackupStatus.store && operation.getStatus()!=BackupStatus.restore))
            setStatus(database, new BackupOperation(databaseName,selectedRevision,BackupStatus.restore,null,0));
        else
            throw new ConcurrentModificationException();

        try {
            //Create an hard database copy into tmp folder
            File destination = new File (registry.getTmpFolder(),databaseName);
            FileUtils.delete(destination);
            FileUtils.copy(new File(registry.getPhysicalDatabsesFolder(),databaseName), destination);
            //Find revision file
            File revisionFile = registry.getFile(database.getDatabaseName(), selectedRevision);
            if (revisionFile==null || !revisionFile.exists() || !revisionFile.isFile())
                throw new Exception("Selected backup revision doesn't exist");
            //Read backup header
            io.setIn(new FileInputStream(revisionFile));
            dataIterator = io.read();
            if (!dataIterator.hasNext())
                throw new Exception("Selected backup revision is empty");
            BackupMeta meta = (BackupMeta)dataIterator.next().getUnit();
            //Reset database
            if (structure) {
                OrientServer.dropDatabase(database);
                OrientServer.createDatabase(database);
                OrientServer.executeDDL(database, meta.getDdl());
            }
            //Start data import
            start(database);
        } catch (Exception ex) {
            BackupOperation oepration = new BackupOperation(databaseName,selectedRevision,BackupStatus.restoreError,null,0);
            operation.setErrorMessage(ex.getMessage());
            setStatus(database, operation);
            throw ex;
        }

    }



    @Override
    protected void execute(BackupRegistry registry, OrientDatabase database) throws Exception {
        //Create msd index structure if needed
        if (database==OrientDatabase.msd)
            msdIndexDao.createDynamicIndexStructure(OrientServer.getDatabase(database));
        try {
            currentDatabase.declareIntent(new OIntentMassiveInsert());
            while (dataIterator.hasNext()) {
                BackupUnit<?> unit = dataIterator.next();
                getStatus(database).setErrorMessage(unit.getUnit().toString());
                consumer.storeUnit(currentDatabase, database, unit.getUnitType(), unit.getUnit());
            }
        } finally {
            currentDatabase.declareIntent(null);
        }
        registry.removeObsoleteRevisions(database.getDatabaseName(),selectedRevision);
    }
}
