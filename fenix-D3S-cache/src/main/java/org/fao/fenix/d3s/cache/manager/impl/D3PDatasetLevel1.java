package org.fao.fenix.d3s.cache.manager.impl;


import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.datasetFilter.impl.DefaultCacheFilter;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.storage.dataset.DefaultStorage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Iterator;

@ApplicationScoped
public class D3PDatasetLevel1 implements CacheManager<DSDDataset,Object[]> {

    @Inject private DefaultStorage storage;
    @Inject private DefaultCacheFilter filterManager;


    @Override
    public void init() throws Exception {
        filterManager.setStorage(storage);
    }

    @Override
    public int getStoreBufferSize() {
        return 0;
    }

    @Override
    public int clean() {
        return 0;
    }

    @Override
    public Iterator<Object[]> load(MeIdentification<DSDDataset> metadata, Order order, Page page) throws Exception {
        return null;
    }

    @Override
    public void store(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data, boolean overwrite, Long timeout) throws Exception {

    }

    @Override
    public void remove(MeIdentification<DSDDataset> metadata) throws Exception {

    }

    @Override
    public StoreStatus status(MeIdentification<DSDDataset> metadata) throws Exception {
        return null;
    }

    @Override
    public Integer size(MeIdentification<DSDDataset> metadata) throws Exception {
        return null;
    }

    @Override
    public void filter(Resource<DSDDataset, Object[]>[] resources, StandardFilter rowsFilter, MeIdentification<DSDDataset> destination, boolean overwrite, Long timeout) throws Exception {

    }

    @Override
    public Iterator<Object[]> filter(MeIdentification<DSDDataset>[] resourcesMetadata, DataFilter filter, Order order) throws Exception {
        return null;
    }


}
