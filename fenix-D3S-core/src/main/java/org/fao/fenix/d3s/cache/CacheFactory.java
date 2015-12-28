package org.fao.fenix.d3s.cache;

import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.manager.CacheManagerFactory;
import org.fao.fenix.d3s.server.init.MainController;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CacheFactory {
    @Inject private CacheManagerFactory cacheManagerFactory;
    @Inject private MainController initializer;

    public CacheManager<DSDDataset, Object[]> getDatasetCacheManager(MeIdentification metadata) throws Exception {
        if (!"true".equalsIgnoreCase(initializer.getInitParameter("cache.disable"))) {
            DSD dsd = metadata!=null ? metadata.getDsd() : null;
            DSDCache cacheInfo = dsd!=null ? dsd.getCache() : null;
            String managerName = cacheInfo!=null ? cacheInfo.getManager() : null;
            String storageName = cacheInfo!=null ? cacheInfo.getStorage() : null;
            return cacheManagerFactory.getInstance(managerName!=null ? managerName : "dataset", storageName!=null ? storageName : "h2");
        } else
            return null;
    }

}
