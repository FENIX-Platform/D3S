package org.fao.fenix.d3s.cache.tools.monitor;


import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Singleton
public class ResourceMonitor {
    public enum Operation { startRead, stopRead, startWrite, stepWrite, stopWrite }

    private Map<String,Long> resourcesSize = new HashMap<>();
    private ArrayList<Thread> writingThreads = new ArrayList<>();
    private int readersCount = 0, writingThreadIndex = 0, writingQueueLength = 0;

    public synchronized void check(Operation operation, String resourceId, long size) throws InterruptedException {
        switch (operation) {
            case startWrite:
                System.out.println("writing: "+resourceId);
                if (resourcesSize.containsKey(resourceId))
                    throw new ConcurrentModificationException();
                resourcesSize.put(resourceId, size);

                writingThreads.add(Thread.currentThread());
                if (++writingQueueLength>1)
                    wait();

                System.out.println("write: " + resourceId);
                break;
            case stepWrite:
                System.out.println("step: "+resourceId);
                resourcesSize.put(resourceId,size+resourcesSize.get(resourceId));
                if (writingQueueLength>1)
                    if (++writingThreadIndex>=writingQueueLength)
                        writingThreadIndex=0;
                notifyAll();
                break;
            case stopWrite:
                System.out.println("write stop: "+resourceId);
                resourcesSize.remove(resourceId);
                writingThreads.remove(writingThreadIndex);
                writingQueueLength--;
                System.out.println("write stop done: " + resourceId);
                notifyAll();
                break;
            case startRead:
                System.out.println("reading: "+resourceId+" - "+size);
                for (Long currentSize = resourcesSize.get(resourceId); currentSize!=null && (size<=0 || size>currentSize); currentSize = resourcesSize.get(resourceId))
                    wait();
                if (++readersCount==1 && writingQueueLength>0) {
                    writingThreads.get(writingThreadIndex).suspend();
                    System.out.println("write suspended");
                }
                System.out.println("read: "+resourceId+" - "+size);
                break;
            case stopRead:
                System.out.println("read stop: "+resourceId+" - "+size);
                if (--readersCount==0 && writingQueueLength>0) {
                    writingThreads.get(writingThreadIndex).resume();
                    System.out.println("write resumed");
                }
                System.out.println("read stopped: "+resourceId+" - "+size);
                break;
        }
    }

    //Utils
    @Inject private Instance<MonitorIterator> iteratorFactory;
    public Iterator<Object[]> getMonitorDataIterator(String resourceId, Iterator<Object[]> source) {
        MonitorIterator iterator = iteratorFactory.get();
        iterator.init(resourceId, source, true);
        return iterator;
    }


}
