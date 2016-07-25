package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.manager.CacheManagerFactory;
import org.fao.fenix.d3s.cache.manager.listener.DatasetAccessInfo;
import org.fao.fenix.d3s.cache.manager.listener.DatasetCacheListener;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;

import java.sql.Connection;

public abstract class ResourceStorageExecutor implements Runnable {

    protected String id;
    private ResourceMonitor monitor;
    private DatasetStorage storage;
    private MeIdentification<DSDDataset> metadata;
    private CacheManagerFactory cacheFactory;
    private ResourceStorageExecutor next;
    public Exception error;
    public boolean done;


    protected ResourceStorageExecutor(MeIdentification<DSDDataset> metadata, CacheManagerFactory cacheFactory, DatasetStorage storage, Table structure, ResourceMonitor monitor) {
        this.monitor = monitor;
        this.storage = storage;
        this.metadata = metadata;
        this.cacheFactory = cacheFactory;
        this.id = structure.getTableName();
    }

    public void add(ResourceStorageExecutor executor) {
        if (next!=null)
            next.add(executor);
        else
            next = executor;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        DatasetAccessInfo datasetInfo = new DatasetAccessInfo(metadata,storage,storage.getTableName(id),null);
        try {
            datasetInfo.setConnection(storage.beginSession(id));
            fireBeginSessionEvent(datasetInfo);
            execute();
        } catch (Exception ex) {
            error = ex;
            try {
                StoreStatus status = storage.loadMetadata(id);
                status.setStatus(StoreStatus.Status.incomplete);
                storage.storeMetadata(id, status);
            } catch (Exception e) { }
        } finally {
            try {
                storage.endSession(id);
            } catch (Exception e) {
                if (error==null)
                    error = e;
                else
                    e.printStackTrace();
            }
            if (id!=null)
                try {
                    //Unlock resource
                    monitor.check(ResourceMonitor.Operation.stopWrite, id, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }

        try {
            fireEndSessionEvent(datasetInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        done = true;

        if (next!=null)
            next.start();

    }

    protected abstract void execute() throws Exception;


    //Events management
    private void fireBeginSessionEvent(DatasetAccessInfo datasetInfo) throws Exception {
        for (DatasetCacheListener listener : cacheFactory.getListeners(metadata))
            listener.updating(datasetInfo);
    }
    private void fireEndSessionEvent(DatasetAccessInfo datasetInfo) throws Exception {
        for (DatasetCacheListener listener : cacheFactory.getListeners(metadata))
            listener.updated(datasetInfo);
    }

}
