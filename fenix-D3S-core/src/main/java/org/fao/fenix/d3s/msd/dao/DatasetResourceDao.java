package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.fao.fenix.d3s.cache.CacheFactory;
import org.fao.fenix.d3s.cache.D3SCache;
import org.fao.fenix.d3s.cache.error.IncompleteException;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.wds.WDSDaoFactory;
import org.fao.fenix.d3s.wds.dataset.WDSDatasetDao;

import javax.inject.Inject;
import java.util.*;

public class DatasetResourceDao extends ResourceDao<DSDDataset,Object[]> {
    @Inject private DatabaseUtils utils;
    @Inject private WDSDaoFactory wdsFactory;
    @Inject private CacheFactory cacheManagerFactory;
    @Inject private CodeListResourceDao clDao;


    @Override
    public Collection<Object[]> loadData(MeIdentification<DSDDataset> metadata) throws Exception {
        DSDDataset dsd = metadata!=null ? metadata.getDsd() : null;
        if (dsd!=null) {
            CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(D3SCache.fixed);
            WDSDatasetDao wdsDao = getDao(metadata);

            //Use extended dsd in case of cache loading and standard dsd other operations
            Language[] languages = dbParameters.getLanguageInfo();
            DSDDataset dsdExtended = cache!=null && languages!=null && languages.length>0 ? dsd.extend(languages) : dsd;

            metadata.setDsd(dsdExtended);
            Iterator<Object[]> data = null;
            if (cache!=null)
                try {
                    data = cache.load(metadata, getOrder(), getPage());
                } catch (IncompleteException ex) {
                    cache.remove(metadata);
                }

            if (data==null) {
                if (wdsDao == null)
                    throw new ClassNotFoundException("Cannot load data. DAO not found");
                metadata.setDsd(dsd);
                data = wdsDao.loadData(metadata);

                if (cache!=null) {
                    cache.store(metadata, utils.getDataIterator(data), true, null, getCodeLists(metadata));
                    metadata.setDsd(dsdExtended);
                    data = cache.load(metadata, getOrder(), getPage());
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

            CacheManager<DSDDataset, Object[]> cache = cacheManagerFactory.getDatasetCacheManager(D3SCache.fixed);
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

            CacheManager<DSDDataset,Object[]> cache = cacheManagerFactory.getDatasetCacheManager(D3SCache.fixed);
            if (cache!=null)
                cache.remove(metadata);

            wdsDao.deleteData(metadata);
        }
    }




    //Utils
    private WDSDatasetDao getDao(MeIdentification<DSDDataset> metadata) {
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
