package org.fao.fenix.d3s.cache.manager;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.manager.listener.Context;
import org.fao.fenix.d3s.cache.manager.listener.DatasetCacheListener;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class CacheManagerFactory {
    private Map<String, Class<? extends CacheManager>> aliasMap = new HashMap<>();
    @Inject private Instance<CacheManager> instanceProducer;
    @Inject private Instance<DatasetCacheListener> listenerInstanceProducer;


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

    //Retrieve cache listeners
    public Collection<DatasetCacheListener> getListeners(MeIdentification metadata) {
        Collection<DatasetCacheListener> list = new LinkedList<>();
        String context = metadata.getDsd().getContextSystem();
        DatasetCacheListener instance;

        for (Iterator<DatasetCacheListener> i = listenerInstanceProducer.select().iterator(); i.hasNext(); )
            if (validateContext(context, instance=i.next()))
                list.add(instance);
        return list;
    }

    private boolean validateContext(String context, DatasetCacheListener listener) {
        Context contextAnnotation = listener.getClass().getAnnotation(Context.class);
        String[] pluginContexts = contextAnnotation!=null ? contextAnnotation.value() : null;
        Set<String> pluginContextsSet = pluginContexts!=null ? new HashSet<>(Arrays.asList(pluginContexts)) : null;
        return pluginContextsSet==null || pluginContextsSet.contains(context);
    }
}
