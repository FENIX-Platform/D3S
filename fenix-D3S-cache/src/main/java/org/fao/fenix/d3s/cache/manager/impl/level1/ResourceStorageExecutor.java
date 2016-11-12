package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.manager.CacheManagerFactory;
import org.fao.fenix.d3s.cache.manager.listener.DatasetAccessInfo;
import org.fao.fenix.d3s.cache.manager.listener.DatasetCacheListener;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.d3s.server.dto.DatabaseStandardsCopy;

import java.sql.Connection;

public abstract class ResourceStorageExecutor implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ResourceStorageExecutor.class);

    protected String id;
    private ResourceMonitor monitor;
    private DatasetStorage storage;
    private MeIdentification<DSDDataset> metadata;
    private CacheManagerFactory cacheFactory;
    private ResourceStorageExecutor next;
    public Exception error;
    public boolean done;
    private DatabaseStandardsCopy globalParametersClone;


    protected ResourceStorageExecutor(DatabaseStandards globalParameters, MeIdentification<DSDDataset> metadata, CacheManagerFactory cacheFactory, DatasetStorage storage, Table structure, ResourceMonitor monitor) {
        this.monitor = monitor;
        this.storage = storage;
        this.metadata = metadata;
        this.cacheFactory = cacheFactory;
        this.id = structure.getTableName();

        globalParametersClone = globalParameters.clone(new DatabaseStandardsCopy());

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
        globalParametersClone.clone(new DatabaseStandards());
        DatasetAccessInfo datasetInfo = new DatasetAccessInfo(metadata,storage,storage.getTableName(id),null);
        try {
            try {
                datasetInfo.setConnection(storage.beginSession(id));
                fireBeginSessionEvent(datasetInfo);
                execute();
            } finally {
                if (id!=null)
                    try {
                        //Unlock resource
                        monitor.check(ResourceMonitor.Operation.stopWrite, id, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
            fireEndSessionEvent(datasetInfo);
        } catch (Exception ex) {
            // TODO: unhandled exception that is not thrown and not visible(ex: index with wrong column id)
            error = ex;
            LOGGER.error(error.getMessage());
            try {
                StoreStatus status = storage.loadMetadata(id);
                status.setStatus(StoreStatus.Status.incomplete);
                storage.storeMetadata(id, status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            try {
                storage.endSession(id);
            } catch (Exception e) {
                if (error==null)
                    error = e;
                else
                    e.printStackTrace();
            }
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
