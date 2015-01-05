package org.fao.fenix.d3s.cache.manager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Properties;

@ApplicationScoped
public class CacheManagerFactory {
    @Inject private Instance<CacheManager> instanceProducer;

    public CacheManager getInstance(String className) throws Exception {
        return getInstance((Class<? extends CacheManager>)Class.forName(className));
    }

    private CacheManager getInstance(Class<? extends CacheManager> managerClass) throws Exception {
        CacheManager manager = instanceProducer.select(managerClass).iterator().next();

        manager.init();

        return manager;
    }


}
