package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.utils.DataUtils;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.fao.fenix.d3s.cache.CacheFactory;
import org.fao.fenix.d3s.cache.D3SCache;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.wds.WDSDaoFactory;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.inject.Inject;
import java.util.*;

public class DatasetResourceDao extends ResourceDao<DSDDataset,Object[]> {
    @Inject private DatabaseUtils utils;
    @Inject private WDSDaoFactory wdsFactory;
    @Inject private CacheFactory cacheManagerFactory;


    @Override
    public Collection<Object[]> loadData(MeIdentification<DSDDataset> metadata) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null) {
            CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(D3SCache.fixed);
            WDSDatasetDao wdsDao = getDao(metadata);

            Iterator<Object[]> data = cache!=null ? cache.load(metadata, getOrder(), getPage()) : null;
            if (data==null) {
                if (wdsDao == null)
                    throw new ClassNotFoundException("Cannot load data. DAO not found");
                data = wdsDao.loadData(metadata);

                if (cache!=null) {
                    cache.store(metadata, utils.getDataIterator(data), true, null);
                    data = cache.load(metadata, getOrder(), getPage());
                }
            }

            return toList(data);
        }

        return null;
    }

    @Override
    protected void insertData(MeIdentification<DSDDataset> metadata, Collection<Object[]> data) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null && metadata.getDsd().getDatasource()!=null)
            updateData(metadata, data, true);
    }

    @Override
    protected void updateData(MeIdentification<DSDDataset> metadata, Collection<Object[]> data, boolean overwrite) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null && metadata.getDsd().getDatasource()!=null) {
            WDSDatasetDao wdsDao = getDao(metadata);
            if (wdsDao==null)
                throw new ClassNotFoundException("Cannot store data. DAO not found");

            CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(D3SCache.fixed);
            if (cache!=null)
                cache.store(metadata, utils.getDataIterator(data), overwrite, null);

            wdsDao.storeData(
                    metadata,
                    cache!=null ? cache.load(metadata, null, null) : data.iterator(),
                    overwrite);
        }
    }

    @Override
    public void deleteData(MeIdentification<DSDDataset> metadata) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null && metadata.getDsd().getDatasource()!=null) {
            WDSDatasetDao wdsDao = getDao(metadata);
            if (wdsDao==null)
                throw new ClassNotFoundException("Cannot store data. DAO not found");

            CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(D3SCache.fixed);
            if (cache!=null)
                cache.remove(metadata);

            wdsDao.deleteData(metadata);
        }
    }




    //Utils
    private WDSDatasetDao getDao(MeIdentification<DSDDataset> metadata) {
        try {
            DSD dsd = metadata!=null ? metadata.getDsd() : null;
            String datasource = dsd!=null ? dsd.getDatasource() : null;
            return datasource!=null ? (WDSDatasetDao) wdsFactory.getInstance(datasource) : null;
        } catch (Exception ex) {
            return null;
        }
    }

}
