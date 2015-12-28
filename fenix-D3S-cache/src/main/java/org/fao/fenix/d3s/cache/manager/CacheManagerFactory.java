package org.fao.fenix.d3s.cache.manager;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.manager.listener.Context;
import org.fao.fenix.d3s.cache.manager.listener.DatasetCacheListener;
import org.fao.fenix.d3s.cache.storage.Storage;
import org.fao.fenix.d3s.cache.storage.StorageName;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class CacheManagerFactory {
    @Inject private Instance<CacheManager> instanceProducer;
    @Inject private Instance<Storage> storageInstanceProducer;
    @Inject private Instance<DatasetCacheListener> listenerInstanceProducer;


    public CacheManager getInstance(String managerName, String storageName) throws Exception {
        CacheManager manager = getManagerInstance(managerName);
        if (manager!=null)
            manager.init(getStorageInstance(storageName));
        return manager;
    }

    private CacheManager getManagerInstance(String managerName) throws Exception {
        if (managerName!=null)
            for (Iterator<CacheManager> i = instanceProducer.select().iterator(); i.hasNext(); ) {
                CacheManager manager = i.next();
                ManagerName info = manager.getClass().getAnnotation(ManagerName.class);
                String name = info!=null ? info.value() : null;
                if (name!=null && name.equals(managerName))
                    return manager;
            }
        return null;
    }
    private Storage getStorageInstance(String storageName) throws Exception {
        if (storageName!=null)
            for (Iterator<Storage> i = storageInstanceProducer.select().iterator(); i.hasNext(); ) {
                Storage storage = i.next();
                StorageName info = storage.getClass().getAnnotation(StorageName.class);
                String name = info!=null ? info.value() : null;
                if (name!=null && name.equals(storageName))
                    return storage;
            }
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
