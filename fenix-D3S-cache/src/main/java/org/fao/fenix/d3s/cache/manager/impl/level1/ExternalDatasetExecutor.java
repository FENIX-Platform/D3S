package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.database.DataIterator;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.manager.CacheManagerFactory;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.d3s.server.dto.DatabaseStandardsCopy;

import java.util.Date;
import java.util.Iterator;


public class ExternalDatasetExecutor extends ResourceStorageExecutor {

    private DatasetStorage storage;
    private ResourceMonitor monitor;
    private Table structure;
    private Iterator<Object[]> data;
    private boolean overwrite;
    private int storePageSize;


    public ExternalDatasetExecutor(DatabaseStandards globalParameters, MeIdentification<DSDDataset> metadata, CacheManagerFactory cacheFactory, DatasetStorage storage, ResourceMonitor monitor, Table structure, Iterator<Object[]> data, boolean overwrite, int pageSize) {
        super(globalParameters, metadata, cacheFactory, storage, structure, monitor);

        this.storage = storage;
        this.monitor = monitor;
        this.structure = structure;
        this.data = data;
        this.overwrite = overwrite;
        storePageSize = pageSize;
    }



    @Override
    protected void execute() throws Exception {
        Date referenceDate = new Date();
        try {
            for (StoreStatus status = storage.store(structure, data, storePageSize, overwrite, referenceDate); status.getStatus() == StoreStatus.Status.loading; status = storage.store(structure, data, storePageSize, false, referenceDate))
                monitor.check(ResourceMonitor.Operation.stepWrite, id, status.getCount());
        } catch (Exception ex) {
            if (data instanceof DataIterator)
                ((DataIterator)data).close();
            throw ex;
        }
    }
}
