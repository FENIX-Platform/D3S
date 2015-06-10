package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.storage.dataset.DefaultStorage;
import org.fao.fenix.d3s.cache.tools.ResourceMonitor;



public class ExternalDatasetExecutor extends ResourceStorageExecutor {

    private static final int SOTRE_PAGE_SIZE = 1000;

    private DatasetStorage storage;
    private Table structure;
    private Iterator<Object[]> data;
    private boolean overwrite;


    public ExternalDatasetExecutor(DatasetStorage storage, ResourceMonitor monitor, Table structure, Iterator<Object[]> data, boolean overwrite) {
        super(structure.getTableName(), monitor);
        this.storage = storage;
        this.structure = structure;
        this.data = data;
        this.overwrite = overwrite;
    }



    @Override
    protected void execute() throws Exception {
        for (StoreStatus status = storage.store(structure, data, SOTRE_PAGE_SIZE, overwrite); status.getStatus() == StoreStatus.Status.loading; status = storage.store(structure, data, SOTRE_PAGE_SIZE, false))
            monitor.check(ResourceMonitor.Operation.stepWrite, id, status.getCount(), false);
    }
}
