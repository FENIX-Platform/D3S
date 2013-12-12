package org.fao.fenix.backup.services.impl;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import org.fao.fenix.backup.dao.RestoreBackup;
import org.fao.fenix.backup.dao.RestoreBackupConsumer;
import org.fao.fenix.backup.dao.StoreBackup;
import org.fao.fenix.backup.dto.BackupOperation;
import org.fao.fenix.msd.dao.cl.CodeListLoad;
import org.fao.fenix.msd.dao.cl.CodeListStore;
import org.fao.fenix.msd.dao.common.CommonsLoad;
import org.fao.fenix.msd.dao.common.CommonsStore;
import org.fao.fenix.msd.dao.dm.DMLoad;
import org.fao.fenix.msd.dao.dm.DMStore;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMDataSource;
import org.fao.fenix.server.tools.orient.OrientDao;
import org.fao.fenix.server.tools.orient.OrientDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Component
public class ManageBackup implements RestoreBackupConsumer {
    @Autowired StoreBackup backupStoreDao;
    @Autowired RestoreBackup backupRestoreDao;
    @Autowired CommonsLoad commonsLoadDao;
    @Autowired CodeListLoad codeListLoadDao;
    @Autowired DMLoad dmLoadDao;
    @Autowired CommonsStore commonsStoreDao;
    @Autowired CodeListStore codeListStoreDao;
    @Autowired DMStore dmStoreDao;



    public BackupOperation statusMsdBackup() {
        return backupStoreDao.getStatus(OrientDatabase.msd);
    }

    public int storeMsdBackup() throws Exception {
        return backupStoreDao.storeNextRevision(OrientDatabase.msd,
                commonsLoadDao.countContactIdentities() + codeListLoadDao.countSystems() + dmLoadDao.countDM(),
                commonsLoadDao.loadContactIdentitiesProducer(),
                codeListLoadDao.loadSystemProducer(true),
                dmLoadDao.loadDMProducer(true)
        );
    }


    public void restoreMsdBackup(Integer revision, boolean structure) throws Exception {
        idMapping.clear();
        backupRestoreDao.restoreDatabase(OrientDatabase.msd,revision,this,structure);
    }


    @Override
    public void storeUnit(OGraphDatabase database, OrientDatabase databaseId, String type, Object data) throws Exception {
        if (databaseId == OrientDatabase.msd)
            storeMsdUnit(database, data);
    }

    private Map<String,String> idMapping = new HashMap<String, String>();
    private void storeMsdUnit(OGraphDatabase database, Object data) throws Exception  {
        if (data instanceof ContactIdentity) {
            String oldId = ((ContactIdentity)data).getId();
            String newId = OrientDao.toString(commonsStoreDao.storeContactIdentity((ContactIdentity)data, database).getIdentity());
            if (oldId!=null)
                idMapping.put(oldId,newId);
        } else if (data instanceof CodeSystem) {
            codeListStoreDao.storeCodeList((CodeSystem)data, database);
        } else if (data instanceof DM) {
            dmStoreDao.storeDatasetMetadata(cleanData((DM)data), database);
        } else
            throw new ClassCastException("Undifined data type for msd backup");
    }

    private DM cleanData(DM data) {
        Collection<ContactIdentity> contactsBuffer = new LinkedList<ContactIdentity>();
        Collection<Publication> pubsBuffer = new LinkedList<Publication>();

        ContactIdentity contact;
        if ((contact = data.getCompiler())!=null)
            contactsBuffer.add(contact);
        if ((contact = data.getOwner())!=null)
            contactsBuffer.add(contact);
        if ((contact = data.getProvider())!=null)
            contactsBuffer.add(contact);

        Collection<ContactIdentity> contacts;
        if ((contacts = data.getContacts())!=null)
            contactsBuffer.addAll(contacts);
        if ((contacts = data.getSources())!=null)
            contactsBuffer.addAll(contacts);

        Collection<Publication> publications;
        if ((publications = data.getPublications())!=null)
            pubsBuffer.addAll(publications);
        if ((publications = data.getNews())!=null)
            pubsBuffer.addAll(publications);
        if ((publications = data.getProcessConceptsDocuments())!=null)
            pubsBuffer.addAll(publications);
        if ((publications = data.getProcessMethodologyDocuments())!=null)
            pubsBuffer.addAll(publications);
        if ((publications = data.getQualityMethodologyDocuments())!=null)
            pubsBuffer.addAll(publications);


        for (ContactIdentity c : contactsBuffer)
            c.setId(idMapping.get(c.getId()));
        for (Publication p : pubsBuffer)
            p.setId(null);

        return data;
    }
}
