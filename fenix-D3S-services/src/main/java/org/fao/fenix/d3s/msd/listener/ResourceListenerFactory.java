package org.fao.fenix.d3s.msd.listener;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.Context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class ResourceListenerFactory {
    @Inject private Instance<ResourceListener> listenerInstanceProducer;

    //Retrieve metadata listeners
    public Collection<ResourceListener> getListeners(String context) {
        Collection<ResourceListener> list = new LinkedList<>();
        ResourceListener instance;
        for (Iterator<ResourceListener> i = listenerInstanceProducer.select().iterator(); i.hasNext(); )
            if (validateContext(context, instance=i.next()))
                list.add(instance);
        return list;
    }

    private boolean validateContext(String context, ResourceListener listener) {
        Context contextAnnotation = listener.getClass().getAnnotation(Context.class);
        String[] pluginContexts = contextAnnotation != null ? contextAnnotation.value() : null;
        Set<String> pluginContextsSet = pluginContexts != null ? new HashSet<>(Arrays.asList(pluginContexts)) : null;
        return pluginContextsSet == null || pluginContextsSet.contains(context);
    }


    //Utils
    public void fireResourceEvent(ResourceEventType type, Object entity, MeIdentification relatedMetadata, String uid, String version, String context) {
        for (ResourceListener listener : getListeners(context))
            switch (type) {
                case insertingMetadata: listener.insertingMetadata((MeIdentification) entity); break;
                case insertedMetadata: listener.insertedMetadata((MeIdentification) entity); break;
                case updatingMetadata: listener.updatingMetadata((MeIdentification) entity); break;
                case updatedMetadata: listener.updatedMetadata((MeIdentification) entity); break;
                case appendingMetadata: listener.appendingMetadata((MeIdentification) entity); break;
                case appendedMetadata: listener.appendedMetadata((MeIdentification) entity); break;
                case removingMetadata: listener.removingMetadata((MeIdentification) entity); break;
                case removedMetadata: listener.removedMetadata(uid, version); break;
                case insertingResource: listener.insertingResource((Resource) entity); break;
                case insertedResource: listener.insertedResource(relatedMetadata); break;
                case updatingResource: listener.updatingResource((Resource) entity); break;
                case updatedResource: listener.updatedResource(relatedMetadata); break;
                case appendingResource: listener.appendingResource((Resource) entity); break;
                case appendedResource: listener.appendedResource(relatedMetadata); break;
                case removingResource: listener.removingResource(relatedMetadata); break;
                case removedResource: listener.removedResource(uid, version); break;
                case updatingDSD: listener.updatingDSD((DSD) entity, relatedMetadata); break;
                case updatedDSD: listener.updatedDSD(relatedMetadata); break;
                case appendingDSD: listener.appendingDSD((DSD) entity, relatedMetadata); break;
                case appendedDSD: listener.appendedDSD(relatedMetadata); break;
                case removingDSD: listener.removingDSD(relatedMetadata); break;
                case removedDSD: listener.removedDSD(relatedMetadata); break;
                case removingData: listener.removingData(relatedMetadata); break;
                case removedData: listener.removedData(relatedMetadata); break;
                case updatingData: listener.updatingData(relatedMetadata); break;
                case updatedData: listener.updatedData(relatedMetadata); break;
            }
    }


}
