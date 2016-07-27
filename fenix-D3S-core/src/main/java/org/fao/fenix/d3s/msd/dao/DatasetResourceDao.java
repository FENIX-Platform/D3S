package org.fao.fenix.d3s.msd.dao;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.commons.utils.database.*;
import org.fao.fenix.d3s.cache.CacheFactory;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.error.IncompleteException;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.manager.DatasetCacheManager;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;
import org.fao.fenix.d3s.wds.WDSDaoFactory;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Iterator;

public class DatasetResourceDao extends ResourceDao<DSDDataset,Object[]> {
    private static final Logger LOGGER = Logger.getLogger(DatasetResourceDao.class);

    @Inject private DatabaseUtils utils;
    @Inject private WDSDaoFactory wdsFactory;
    @Inject private CacheFactory cacheManagerFactory;
    @Inject private CodeListResourceDao clDao;


    @Override
    public void fetch(MeIdentification<DSDDataset> metadata) throws Exception {
        CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(metadata);
        if (cache==null)
            throw new UnsupportedOperationException();

        Collection<Object[]> data = loadData(metadata, new Page(0, 1), null);
        if (data!=null)
            for (Object[] row : data);
    }

    @Override
    public Long getSize(MeIdentification<DSDDataset> metadata) throws Exception {
        CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(metadata);
        return cache!=null ? cache.size(metadata) : null;
    }

    @Override
    public Collection<Object[]> loadData(MeIdentification<DSDDataset> metadata) throws Exception {
        return loadData(metadata, getPage(), getOrder());
    }
    private Collection<Object[]> loadData(MeIdentification<DSDDataset> metadata, Page pagination, Order ordering) throws Exception {
        Iterator<Object[]> data = null;
        DSDDataset dsd = metadata!=null ? metadata.getDsd() : null;

        if (dsd!=null) {
            DatasetCacheManager cache = (DatasetCacheManager) cacheManagerFactory.getDatasetCacheManager(metadata);
            if (cache!=null) {
                //Fill cache
                StoreStatus status = cache.status(metadata);
                Date dataUpdateDateByMetadata = getDataUpdateDate(metadata);
                if (status==null || status.getStatus()==StoreStatus.Status.incomplete || (dataUpdateDateByMetadata!=null && status.getLastUpdate().before(dataUpdateDateByMetadata))) {
                    cache.remove(metadata);
                    cache.store(metadata, loadRawData(metadata), true, null, getCodeLists(metadata));
                }
                //Add labels to the required dataset
                Language[] languages = dbParameters.getLanguageInfo();
                if (languages!=null && languages.length>0)
                    dsd.extend(languages);
                //Load data
                data = cache.load(metadata, ordering, pagination);
                LOGGER.debug("Loaded data from cache: uid = "+metadata.getUid()+" - version = "+metadata.getVersion()+" data = "+(data!=null ? true : false));
            } else
                data = loadRawData(metadata);
        }

        return data!=null ? toList(data) : null;
    }
    private Iterator<Object[]> loadRawData(MeIdentification<DSDDataset> metadata) throws Exception {
        WDSDatasetDao wdsDao = getDao(metadata);
        return wdsDao!=null ? wdsDao.loadData(metadata) : new LinkedList<Object[]>().iterator();
    }

    @Override
    protected void insertData(MeIdentification<DSDDataset> metadata, Collection<Object[]> data) throws Exception {
        updateData(metadata, data, true);
    }

    @Override
    protected void updateData(MeIdentification<DSDDataset> metadata, Collection<Object[]> data, boolean overwrite) throws Exception {
        if (getDatasource(metadata)!=null) {
            WDSDatasetDao wdsDao = getDao(metadata);
            if (wdsDao==null)
                throw new ClassNotFoundException("Cannot store data. DAO not found");

            DatasetCacheManager cache = (DatasetCacheManager) cacheManagerFactory.getDatasetCacheManager(metadata);
            if (cache != null)
                cache.store(metadata, utils.getDataIterator(data), overwrite, null, getCodeLists(metadata));

            wdsDao.storeData(
                    metadata,
                    cache!=null ? cache.load(metadata, null, null) : (data!=null ? data.iterator() : null),
                    overwrite);
        }
    }

    @Override
    public void deleteData(MeIdentification<DSDDataset> metadata) throws Exception {
        if (getDatasource(metadata)!=null) {
            WDSDatasetDao wdsDao = getDao(metadata);
            if (wdsDao==null)
                throw new ClassNotFoundException("Cannot store data. DAO not found");

            CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(metadata);
            if (cache!=null)
                cache.remove(metadata);

            wdsDao.deleteData(metadata);
        }
    }



/*
    //Synchronize metadata and data write operations respect to cache activities
    @Override
    public MeIdentification<DSDDataset> updateMetadata(MeIdentification<DSDDataset> metadata, boolean overwrite, boolean transaction) throws Exception {
        //Retrieve new data update date
        Date updateDate = getDataUpdateDate(metadata);
        //Retrieve cache manager
        CacheManager<DSDDataset, Object[]> cache = cacheManagerFactory.getDatasetCacheManager(metadata);
        DatasetStorage cacheStorage = cache!=null ? (DatasetStorage) cache.getStorage() : null;
        //If data is changing from other process and last update date is changing throw exception
        if (updateDate!=null && cacheStorage!=null && cacheStorage.containsSession(cacheStorage.getTableName(new Table(metadata).getTableName())))
            throw new BadRequestException("cannot update metadata because another process is loading data into the first cache level");
        else //Update metadata end retrieve a consistent metadata for cache manager identification
            return super.updateMetadata(metadata, overwrite, transaction);
    }
*/
    @Override
    public void deleteMetadata(boolean transaction, MeIdentification<DSDDataset>... metadataList) throws Exception {
        if (metadataList!=null)
            for (MeIdentification metadata : metadataList) {
                CacheManager<DSDDataset, Object[]> cache = cacheManagerFactory.getDatasetCacheManager(metadata);
                if (cache!=null)
                    cache.remove(metadata);
            }

        super.deleteMetadata(transaction, metadataList);
    }



    //Utils
    private WDSDatasetDao getDao(MeIdentification<DSDDataset> metadata) throws Exception {
        try {
            String datasource = getDatasource(metadata);
            return datasource!=null ? (WDSDatasetDao) wdsFactory.getInstance(datasource) : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private Collection<Resource<DSDCodelist,Code>> getCodeLists(MeIdentification<DSDDataset> metadata) throws Exception {
        Collection<Resource<DSDCodelist,Code>> codeListsResource = new LinkedList<>();
        DSDDataset dsd = metadata!=null ? metadata.getDsd() : null;
        Collection<DSDColumn> columns = dsd!=null ? dsd.getColumns() : null;
        if (columns!=null)
            for (DSDColumn column : dsd.getColumns()) {
                if (column.getDataType()==DataType.code) {
                    DSDDomain domain = column.getDomain();
                    Collection<OjCodeList> codeLists = domain!=null ? domain.getCodes() : null;
                    OjCodeList codeList = codeLists!=null && codeLists.size()>0 ? codeLists.iterator().next() : null;
                    MeIdentification<DSDCodelist> codeListMetadata = codeList!=null ? codeList.getLinkedCodeList() : null;
                    Collection<Code> codeListData = codeListMetadata!=null ? clDao.loadData(codeListMetadata) : null;
                    if (codeListData!=null && codeListData.size()>0)
                        codeListsResource.add(new Resource(codeListMetadata,codeListData));
                }
            }

        return codeListsResource;
    }


    private String getDatasource(MeIdentification<DSDDataset> metadata) {
        DSD dsd = metadata!=null ? metadata.getDsd() : null;
        String[] datasources = dsd!=null ? dsd.getDatasources() : null;
        return datasources!=null && datasources.length>0 ? datasources[0] : null;
    }


    private Date getDataUpdateDate(MeIdentification<DSDDataset> metadata) {
        MeMaintenance meMaintenance = metadata!=null ? metadata.getMeMaintenance() : null;
        SeUpdate seUpdate = meMaintenance!=null ? meMaintenance.getSeUpdate() : null;
        return seUpdate!=null ? seUpdate.getUpdateDate() : null;
    }



}
