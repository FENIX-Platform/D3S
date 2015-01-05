package org.fao.fenix.d3s.cache.manager.impl;

import org.fao.fenix.d3s.cache.datasetFilter.impl.DefaultCacheFilter;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.storage.impl.H2DatasetDefaultStorage;

import javax.inject.Inject;


public class D3SLevel1  implements CacheManager {

    @Inject private H2DatasetDefaultStorage storage;
    @Inject private DefaultCacheFilter filterManager;


    @Override
    public void init() throws Exception {
        filterManager.setStorage(storage);
    }
}
