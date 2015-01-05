package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.manager.CacheManagerFactory;
import org.fao.fenix.d3s.wds.WDSDaoFactory;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.inject.Inject;
import java.util.*;

public class DatasetResourceDao extends ResourceDao<Object[]> {
    @Inject private WDSDaoFactory wdsFactory;


    @Override
    public Collection<Object[]> loadData(MeIdentification metadata) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null) {
            CacheManager cache = getCurrentCacheManager();
            WDSDatasetDao wdsDao = getDao(metadata);

            Iterator<Object[]> data = cache!=null ? cache.load(metadata, getOrder(), getPage()) : null;
            if (data==null) {
                if (wdsDao == null)
                    throw new ClassNotFoundException("Cannot load data. DAO not found");
                data = wdsDao.loadData(metadata);
            }

            return toList(data);
        }

        return null;
    }

    @Override
    protected void insertData(MeIdentification metadata, Collection<Object[]> data) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null && metadata.getDsd().getDatasource()!=null)
            updateData(metadata, data, true);
    }

    @Override
    protected void updateData(MeIdentification metadata, Collection<Object[]> data, boolean overwrite) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null && metadata.getDsd().getDatasource()!=null) {
            WDSDatasetDao wdsDao = getDao(metadata);
            if (wdsDao==null)
                throw new ClassNotFoundException("Cannot store data. DAO not found");

            wdsDao.storeData(metadata, data.iterator(), overwrite);
        }
    }

    @Override
    public void deleteData(MeIdentification metadata) throws Exception {
        if (metadata!=null && metadata.getDsd()!=null && metadata.getDsd().getDatasource()!=null) {
            WDSDatasetDao wdsDao = getDao(metadata);
            if (wdsDao==null)
                throw new ClassNotFoundException("Cannot store data. DAO not found");

            wdsDao.deleteData(metadata);
        }
    }


    //Dataset cache manager injection management
    private static String cacheManagerClassName;
    @Inject private CacheManagerFactory cacheManagerPluginFactory;

    public void init(String cacheManagerClassName) throws Exception {
        DatasetResourceDao.cacheManagerClassName = cacheManagerClassName;
    }

    private CacheManager getCurrentCacheManager() {
        try {
            return cacheManagerPluginFactory.getInstance(cacheManagerClassName);
        } catch (Exception ex) {
            return null;
        }
    }



    //Utils
    private WDSDatasetDao getDao(MeIdentification metadata) {
        try {
            DSD dsd = metadata!=null ? metadata.getDsd() : null;
            String datasource = dsd!=null ? dsd.getDatasource() : null;
            return datasource!=null ? (WDSDatasetDao) wdsFactory.getInstance(datasource) : null;
        } catch (Exception ex) {
            return null;
        }
    }

}
