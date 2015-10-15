package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;

import java.util.Date;


public class InternalDatasetExecutor extends ResourceStorageExecutor {

    private DatasetStorage storage;
    private boolean overwrite;
    private DataFilter filter;
    private Table structure;
    private Table[] sourceTables;

    public InternalDatasetExecutor(DatasetStorage storage, ResourceMonitor monitor, Table structure, DataFilter filter, boolean overwrite, Table... sourceTables) {
        super(structure.getTableName(),monitor);
        this.storage = storage;
        this.overwrite = overwrite;
        this.filter = filter;
        this.structure = structure;
        this.sourceTables = sourceTables;
    }



    @Override
    protected void execute() throws Exception {
        storage.store(structure,filter,overwrite, new Date(), sourceTables);
    }
}
