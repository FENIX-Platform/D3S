package org.fao.fenix.d3s.msd.listener;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

public class MetadataListenerFactory {
    @Inject private Instance<MetadataListener> listenerInstanceProducer;


    //Retrieve cache listeners
    public Collection<MetadataListener> getListeners(String context) {
        Collection<MetadataListener> list = new LinkedList<>();
        MetadataListener instance;

        for (Iterator<MetadataListener> i = listenerInstanceProducer.select().iterator(); i.hasNext(); )
            if (validateContext(context, instance=i.next()))
                list.add(instance);
        return list;
    }

    private boolean validateContext(String context, MetadataListener listener) {
        Context contextAnnotation = listener.getClass().getAnnotation(Context.class);
        String[] pluginContexts = contextAnnotation!=null ? contextAnnotation.value() : null;
        Set<String> pluginContextsSet = pluginContexts!=null ? new HashSet<>(Arrays.asList(pluginContexts)) : null;
        return pluginContextsSet==null || pluginContextsSet.contains(context);
    }

}
