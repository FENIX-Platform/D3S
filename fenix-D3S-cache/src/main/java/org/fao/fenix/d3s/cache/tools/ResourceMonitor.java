package org.fao.fenix.d3s.cache.tools;


import javax.inject.Singleton;
import javax.naming.NameAlreadyBoundException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ResourceMonitor {
    public enum Operation { startRead, startWrite, stepWrite, stopWrite }

    private Map<String,Long> resourcesSize = new HashMap<>();

    public synchronized void check(Operation operation, String resourceId, long size, boolean ordering) throws InterruptedException {
        switch (operation) {
            case startWrite:
                System.out.println("writing: "+resourceId);
                if (resourcesSize.containsKey(resourceId))
                    throw new ConcurrentModificationException();
                resourcesSize.put(resourceId, size);
                System.out.println("write: " + resourceId);
                break;
            case stepWrite:
                System.out.println("step: "+resourceId);
                resourcesSize.put(resourceId,size+resourcesSize.get(resourceId));
                notifyAll();
                break;
            case stopWrite:
                System.out.println("stopping: "+resourceId);
                resourcesSize.remove(resourceId);
                System.out.println("stop: " + resourceId);
                notifyAll();
                break;
            case startRead:
                System.out.println("reading: "+resourceId+" - "+size+" - "+ordering);
                for (Long currentSize = resourcesSize.get(resourceId); currentSize!=null && (ordering || size<=0 || size>currentSize); currentSize = resourcesSize.get(resourceId))
                    wait();
                System.out.println("read: "+resourceId+" - "+size+" - "+ordering);
                break;
        }
    }

}
