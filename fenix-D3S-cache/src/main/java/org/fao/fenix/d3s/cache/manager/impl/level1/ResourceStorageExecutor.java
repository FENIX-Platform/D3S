package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.manager.CacheManagerFactory;
import org.fao.fenix.d3s.cache.manager.listener.DatasetAccessInfo;
import org.fao.fenix.d3s.cache.manager.listener.DatasetCacheListener;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;

import java.sql.Connection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executors;

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
        Connection connection = null;
        try {
            connection = storage.beginSession(id);
            DatasetAccessInfo datasetInfo = new DatasetAccessInfo(metadata,storage,storage.getTableName(id),connection);
            fireBeginSessionEvent(datasetInfo);
            execute();
            fireEndSessionEvent(datasetInfo);
        } catch (Exception ex) {
            error = ex;
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


        done = true;

        if (next!=null)
            next.start();

    }

    protected abstract void execute() throws Exception;


    //Events management
    private void fireBeginSessionEvent(DatasetAccessInfo datasetInfo) {
        for (DatasetCacheListener listener : cacheFactory.getListeners(metadata))
            listener.updating(datasetInfo);
    }
    private void fireEndSessionEvent(DatasetAccessInfo datasetInfo) {
        for (DatasetCacheListener listener : cacheFactory.getListeners(metadata))
            listener.updated(datasetInfo);
    }

}
