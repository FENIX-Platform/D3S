package org.fao.fenix.d3s.msd.dao;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.fao.fenix.d3s.cache.CacheFactory;
import org.fao.fenix.d3s.cache.error.IncompleteException;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.manager.DatasetCacheManager;
import org.fao.fenix.d3s.wds.WDSDaoFactory;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.inject.Inject;
import java.util.*;

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

        for (Object[] row : loadData(metadata, new Page(0, 1), null));
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
        DSDDataset dsd = metadata!=null ? metadata.getDsd() : null;
        if (dsd!=null) {
            DatasetCacheManager cache = (DatasetCacheManager) cacheManagerFactory.getDatasetCacheManager(metadata);
            WDSDatasetDao wdsDao = getDao(metadata);

            //Use extended dsd in case of cache loading and standard dsd other operations
            Language[] languages = dbParameters.getLanguageInfo();
            DSDDataset dsdExtended = cache!=null && languages!=null && languages.length>0 ? dsd.extend(true, languages) : dsd;

            metadata.setDsd(dsdExtended);
            Iterator<Object[]> data = null;
            if (cache!=null)
                try {
                    data = cache.load(metadata, ordering, pagination);
                    LOGGER.debug("Loaded data from cache: uid = "+metadata.getUid()+" - version = "+metadata.getVersion()+" data = "+(data!=null ? true : false));
                } catch (IncompleteException ex) {
                    LOGGER.debug("IncompleteException from cache: uid = "+metadata.getUid()+" - version = "+metadata.getVersion());
                    cache.remove(metadata);
                }

            if (data==null) {
                if (wdsDao == null) {
                    data = new LinkedList<Object[]>().iterator();
                } else {
                    metadata.setDsd(dsd);
                    data = wdsDao.loadData(metadata);
                }

                if (cache!=null && wdsDao!=null) {
                    cache.store(metadata, utils.getDataIterator(data), true, null, getCodeLists(metadata));
                    if (languages!=null && languages.length>0)
                        dsd.extend(false,languages);
                    metadata.setDsd(dsd);
                    data = cache.load(metadata, ordering, pagination);
                }
            }

            return toList(data);
        }

        return null;
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




    //Synchronize metadata and data write operations respect to cache activities
    @Override
    public MeIdentification<DSDDataset> updateMetadata(MeIdentification<DSDDataset> metadata, boolean overwrite, boolean transaction) throws Exception {
        //Retrieve new data update date
        MeMaintenance meMaintenance = metadata!=null ? metadata.getMeMaintenance() : null;
        SeUpdate seUpdate = meMaintenance!=null ? meMaintenance.getSeUpdate() : null;
        Date updateDate = seUpdate!=null ? seUpdate.getUpdateDate() : null;
        //Update metadata end retrieve a consistent metadata for cache manager identification
        metadata = super.updateMetadata(metadata, overwrite, transaction);
        //Retrieve cache manager
        CacheManager<DSDDataset, Object[]> cache = cacheManagerFactory.getDatasetCacheManager(metadata);
        //If data is changed remove cache info
        if (updateDate!=null && cache!=null)
            cache.remove(metadata);
        //Return updated metadata
        return metadata;
    }

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


}
