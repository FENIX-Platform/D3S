package org.fao.fenix.d3s.cache.manager.impl;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.dto.dataset.WriteTable;
import org.fao.fenix.d3s.cache.error.IncompleteException;
import org.fao.fenix.d3s.cache.manager.DatasetCacheManager;
import org.fao.fenix.d3s.cache.manager.ManagerName;
import org.fao.fenix.d3s.cache.manager.listener.DatasetAccessInfo;
import org.fao.fenix.d3s.cache.manager.listener.DatasetCacheListener;
import org.fao.fenix.d3s.cache.manager.CacheManagerFactory;
import org.fao.fenix.d3s.cache.manager.impl.level1.ExternalDatasetExecutor;
import org.fao.fenix.d3s.cache.manager.impl.level1.LabelDataIterator;
import org.fao.fenix.d3s.cache.manager.impl.level1.ResourceStorageExecutor;
import org.fao.fenix.d3s.cache.storage.Storage;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;


@ApplicationScoped
@ManagerName("dataset")
public class D3SDatasetLevel1 implements DatasetCacheManager {

    private static final int SOTRE_PAGE_SIZE = 50;

    @Inject private CacheManagerFactory listenersFactory;
    @Inject private DatabaseUtils utils;
    @Inject private ResourceMonitor monitor;
    private DatasetStorage storage;

    @Override
    public void init(Storage storage) throws Exception {
        if (storage instanceof DatasetStorage)
            this.storage = (DatasetStorage) storage;
        else
            throw new UnsupportedOperationException("The selected cache manager can work only on dataset cache storage");
    }

    @Override
    public int getStoreBufferSize() {
        return SOTRE_PAGE_SIZE;
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public int clean() throws Exception {
        return storage!=null ? storage.clean() : 0;
    }

    @Override
    public Iterator<Object[]> load(MeIdentification<DSDDataset> metadata, Order order, Page page) throws Exception {
        String id = getID(metadata);
        boolean ordering = order!=null && order.size()>0;
        int size = !ordering && page!=null && page.perPage>0 ? page.skip+page.length : 0;
        //Check resource status and resource (and related codelists) last update date
        StoreStatus status = storage.loadMetadata(id);
        if (status!=null) {
            if (status.getStatus()==StoreStatus.Status.incomplete) //Check status
                throw new IncompleteException(id);
            if (!checkUpdateDateIsValid(metadata,status)) {
                storage.delete(id);
                return null;
            }
            monitor.check(ResourceMonitor.Operation.startRead, id, size);
            try {
                Iterator<Object[]> data = storage.load(order, page, new Table(metadata));
                if (data==null) {
                    monitor.check(ResourceMonitor.Operation.stopRead, id, 0);
                    return null;
                } else
                    return monitor.newMonitorDataIterator(id, data);
            } catch (Exception ex) {
                //Unlock resources write
                monitor.check(ResourceMonitor.Operation.stopRead, id, 0);
                //Throw error
                throw ex;
            }
        } else
            return null;
    }

    @Override
    public void store(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data, boolean overwrite, Long timeout) throws Exception {
        store(metadata,data,overwrite,timeout,null);
    }
    @Override
    public void store(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data, boolean overwrite, Long timeout, Collection<Resource<DSDCodelist,Code>> codeLists) throws Exception {
        //Lock resource
        String id = getID(metadata);
        try {
            monitor.check(ResourceMonitor.Operation.startWrite, id, 0);
            Table tableMetadata = new WriteTable(metadata);
            data = new LabelDataIterator(data,tableMetadata,metadata.getDsd(),codeLists);
            //Create table if not exists
            StoreStatus status = storage.loadMetadata(id);
            //Verify last update date
            if (!checkUpdateDateIsValid(metadata,status)) {
                storage.delete(id);
                status = null;
            }
            //Create table if needed
            if (status == null)
                storage.create(tableMetadata, timeout != null ? new Date(System.currentTimeMillis() + timeout) : null);
            //Store data and unlock resource
            ResourceStorageExecutor executor = new ExternalDatasetExecutor(metadata, listenersFactory, storage, monitor, tableMetadata, data, overwrite, SOTRE_PAGE_SIZE);
            //executor.addListener(this);
            executor.start();
        } catch (Exception ex) {
            //Unlock resource
            monitor.check(ResourceMonitor.Operation.stopWrite, id, 0);
            //Throw error
            throw ex;
        }
    }



    @Override
    public void remove(MeIdentification<DSDDataset> metadata) throws Exception {
        //Lock resource
        String id = getID(metadata);
        monitor.check(ResourceMonitor.Operation.startWrite, id, 0);
        try {
            //Fire starting remove operation event
            Connection connection = storage.getConnection();
            try {
                DatasetAccessInfo datasetInfo = new DatasetAccessInfo(metadata, storage, storage.getTableName(id), null);
                for (DatasetCacheListener listener : listenersFactory.getListeners(metadata))
                    listener.removing(datasetInfo);
            } finally {
                connection.close();
            }
            //Delete
            storage.delete(id);
        } finally {
            //Unlock resource
            monitor.check(ResourceMonitor.Operation.stopWrite, id, 0);
        }
    }

    @Override
    public StoreStatus status(MeIdentification<DSDDataset> metadata) throws Exception {
        return storage.loadMetadata(getID(metadata));
    }

    @Override
    public Long size(MeIdentification<DSDDataset> metadata) throws Exception {
        StoreStatus status = storage.loadMetadata(getID(metadata));
        return status!=null ? status.getCount() : null;
    }

    @Override
    public String getID(MeIdentification metadata) {
        return metadata!=null ? metadata.getUid() + (metadata.getVersion()!=null ? '|'+metadata.getVersion() : "") : null;
    }


    //Utils
    //Retrieve last update dates for dataset and related codelists
    private Collection<Date> getDatasetLastUpdateDates(MeIdentification<DSDDataset> metadata) {
        Collection<Date> dates = new LinkedList<>();
        //Add dataset update date
        Date date = getLastUpdateDate(metadata);
        if (date!=null)
            dates.add(date);
        //Add columns update date
        DSDDataset dsd = metadata.getDsd();
        Collection<DSDColumn> columns = dsd!=null ? dsd.getColumns() : null;
        if (columns!=null)
            for (DSDColumn column : columns) {
                DSDDomain domain = column.getDomain();
                Collection<OjCodeList> codeLists = domain!=null ? domain.getCodes() : null;
                if (codeLists!=null)
                    for (OjCodeList codeList : codeLists)
                        if ((date = getLastUpdateDate(codeList.getLinkedCodeList())) != null)
                            dates.add(date);
            }
        //Return list
        return dates;
    }
    private Date getLastUpdateDate(MeIdentification<?> metadata) {
        MeMaintenance meMaintenance = metadata!=null ? metadata.getMeMaintenance() : null;
        SeUpdate seUpdate = meMaintenance!=null ? meMaintenance.getSeUpdate() : null;
        return seUpdate!=null ? seUpdate.getUpdateDate() : null;
    }


    private boolean checkUpdateDateIsValid(MeIdentification<DSDDataset> metadata, StoreStatus status) {
        if (metadata!=null && status!=null) {
            Date cacheLastUpdate = status.getLastUpdate();
            for (Date lastUpdate : getDatasetLastUpdateDates(metadata)) //Check last update date
                if (cacheLastUpdate.before(lastUpdate))
                    return false;
        }
        return true;
    }

}
