package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.storage.dataset.DefaultStorage;
import org.fao.fenix.d3s.cache.tools.ResourceMonitor;



public class ExternalDatasetExecutor extends ResourceStorageExecutor {

    private static final int SOTRE_PAGE_SIZE = 1000;

    private DatasetStorage storage;
    private StoreStatus status;
    private Iterator<Object[]> data;
    private boolean overwrite;


    public ExternalDatasetExecutor(DatasetStorage storage, ResourceMonitor monitor, StoreStatus status, String id, Iterator<Object[]> data, boolean overwrite) {
        super(id,monitor);
        this.storage = storage;
        this.status = status;
        this.data = data;
        this.overwrite = overwrite;
    }



    @Override
    protected void execute() throws Exception {
        for (status = storage.store(id, data, SOTRE_PAGE_SIZE, overwrite); status.getStatus() == StoreStatus.Status.loading; status = storage.store(id, data, SOTRE_PAGE_SIZE, false))
            monitor.check(ResourceMonitor.Operation.stepWrite, id, status.getCount(), false);
    }
}
