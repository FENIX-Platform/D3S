package org.fao.fenix.d3s.cache.manager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@ApplicationScoped
public class CacheManagerFactory {
    private Map<String, Class<? extends CacheManager>> aliasMap = new HashMap<>();
    @Inject private Instance<CacheManager> instanceProducer;


    public void addAlias(String alias, String className) throws ClassNotFoundException {
        if (className.trim().length()>0)
            aliasMap.put(alias, (Class<? extends CacheManager>) Class.forName(className));
    }

    public CacheManager getInstance(String name) throws Exception {
        Class<? extends CacheManager> instanceClass = aliasMap.get(name);
        if (instanceClass!=null) {
            CacheManager manager = instanceProducer.select(instanceClass).iterator().next();
            manager.init();
            return manager;
        } else
            return null;
    }
}
