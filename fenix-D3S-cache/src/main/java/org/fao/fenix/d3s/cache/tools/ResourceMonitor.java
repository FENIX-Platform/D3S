package org.fao.fenix.d3s.cache.tools;


import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ResourceMonitor {
    public enum Operation { startRead, startWrite, stepWrite, stopWrite, delete }

    private Map<String,Integer> resourcesSize = new HashMap<>();

    public synchronized void check(Operation operation, String resourceId, int size, boolean ordering) throws InterruptedException {
        switch (operation) {
            case startWrite:
                resourcesSize.put(resourceId,size);
                break;
            case stepWrite:
                resourcesSize.put(resourceId,size+resourcesSize.get(resourceId));
                notifyAll();
                break;
            case stopWrite:
                resourcesSize.remove(resourceId);
                notifyAll();
                break;
            case startRead:
                for (Integer currentSize = resourcesSize.get(resourceId); currentSize!=null && (ordering || size<=0 || size>currentSize); currentSize = resourcesSize.get(resourceId))
                    wait();
                break;
            case delete:
                while (resourcesSize.get(resourceId)!=null)
                    wait();
                break;
        }
    }

}
