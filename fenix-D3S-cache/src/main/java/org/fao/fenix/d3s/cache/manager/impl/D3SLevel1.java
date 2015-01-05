package org.fao.fenix.d3s.cache.manager.impl;

import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.datasetFilter.impl.DefaultCacheFilter;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.storage.impl.H2DatasetDefaultStorage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Iterator;


@ApplicationScoped
public class D3SLevel1 implements CacheManager {

    private boolean initialized = false;
    @Inject private H2DatasetDefaultStorage storage;
    @Inject private DefaultCacheFilter filterManager;


    @Override
    public void init() throws Exception {
        if (!initialized) {
            filterManager.setStorage(storage);
            initialized = true;
        }
    }

    @Override
    public Iterator<Object[]> load(MeIdentification<DSDDataset> metadata, Order order, Page page) throws Exception {
        return null;
    }

    @Override
    public void load(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data) throws Exception {

    }
}
