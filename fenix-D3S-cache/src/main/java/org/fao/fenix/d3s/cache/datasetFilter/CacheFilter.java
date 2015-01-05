package org.fao.fenix.d3s.cache.datasetFilter;

import org.fao.fenix.d3s.cache.storage.DatasetStorage;

public abstract class CacheFilter {

    private DatasetStorage storage;


    public DatasetStorage getStorage() {
        return storage;
    }

    public void setStorage(DatasetStorage storage) {
        this.storage = storage;
    }
}
