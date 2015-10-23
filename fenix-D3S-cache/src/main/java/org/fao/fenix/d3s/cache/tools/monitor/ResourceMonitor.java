package org.fao.fenix.d3s.cache.tools.monitor;


import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Singleton
@Path("cache/monitor")
@Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ResourceMonitor {
    public enum Operation { startRead, stopRead, startWrite, stepWrite, stopWrite }

    private static final long WRITE_TIMEOUT = 5*60*1000; //5 minutes
    private Map<String,Long> resourcesSize = new HashMap<>();
    private ArrayList<Thread> writingThreads = new ArrayList<>();
    private int readersCount = 0, writingThreadIndex = 0, writingQueueLength = 0;

    public synchronized void check(Operation operation, String resourceId, long size) throws InterruptedException {
        Thread currentThread = Thread.currentThread();
        switch (operation) {
            case startWrite:
                //System.out.println("writing: "+resourceId);

                if (resourcesSize.containsKey(resourceId))
                    throw new ConcurrentModificationException();
                resourcesSize.put(resourceId, size);

                writingThreads.add(currentThread);
                writingQueueLength++;

                //System.out.println("for: " + resourceId + " - threads size: "+writingThreads.size()+" - queue length: "+writingQueueLength);

                while (readersCount>0 || (writingQueueLength>1 && writingThreads.get(writingThreadIndex).getId()!=currentThread.getId())) {
                    //System.out.println("write lock: "+resourceId+" - readers: "+readersCount+" - index: "+writingThreadIndex+" - indexed thread: "+writingThreads.get(writingThreadIndex).getId()+ " - current thread: "+currentThread.getId());
                    long time = System.currentTimeMillis();
                    wait(WRITE_TIMEOUT);
                    if ((System.currentTimeMillis()-time)>WRITE_TIMEOUT)
                        throw new InterruptedException("Timeout on write operation");
                }

                //System.out.println("write: " + resourceId + " - threads size: "+writingThreads.size()+" - queue length: "+writingQueueLength);
                break;
            case stepWrite:
                //System.out.println("step: "+resourceId);

                resourcesSize.put(resourceId,size+resourcesSize.get(resourceId));
                writingThreads.set(writingThreadIndex, currentThread); //refresh current writing thread for the resource

                if (writingQueueLength > 1 && ++writingThreadIndex >= writingQueueLength)
                    writingThreadIndex = 0;

                notifyAll();

                while (readersCount>0 || (writingQueueLength>1 && writingThreads.get(writingThreadIndex).getId()!=currentThread.getId())) {
                    //System.out.println("step lock: "+resourceId+" - readers: "+readersCount+" - index: "+writingThreadIndex+" - indexed thread: "+writingThreads.get(writingThreadIndex).getId()+ " - current thread: "+currentThread.getId());
                    long time = System.currentTimeMillis();
                    wait(WRITE_TIMEOUT);
                    if ((System.currentTimeMillis()-time)>WRITE_TIMEOUT)
                        throw new InterruptedException("Timeout on write operation");
                }


                //System.out.println("step done: " + resourceId + " - queue index: "+writingThreadIndex+" - queue length: "+writingQueueLength);

                break;
            case stopWrite:
                //System.out.println("write stop: "+resourceId);

                resourcesSize.remove(resourceId);
                writingThreads.remove(writingThreadIndex);
                if (writingThreadIndex >= --writingQueueLength)
                    writingThreadIndex = 0;

                //System.out.println("write stop done: " + resourceId + " - threads size: "+writingThreads.size()+" - queue length: "+writingQueueLength);

                notifyAll();
                break;
            case startRead:
                //System.out.println("reading: "+resourceId+" - size: "+size+" - thread: "+currentThread.getId());

                for (Long currentSize = resourcesSize.get(resourceId); currentSize!=null && (size<=0 || size>currentSize); currentSize = resourcesSize.get(resourceId))
                    wait();
                readersCount++;

                //System.out.println("read: "+resourceId+" - readers: "+readersCount+" - thread: "+currentThread.getId());
                break;
            case stopRead:
                //System.out.println("read stop: "+resourceId+" - size: "+size+" - thread: "+currentThread.getId());

                if (--readersCount==0) {
                    //System.out.println("notify after read");
                    notifyAll();
                }

                //System.out.println("read stopped: " + resourceId + " - readers: " + readersCount + " - thread: " + currentThread.getId());
                break;
        }
    }

    //Utils
    @Inject private Instance<MonitorIterator> iteratorFactory;
    public Iterator<Object[]> newMonitorDataIterator(String resourceId, Iterator<Object[]> source) {
        MonitorIterator iterator = iteratorFactory.get();
        iterator.init(resourceId, source, true);
        return iterator;
    }


    //As a status bean
    public Map<String, Long> getResourcesSize() {
        return resourcesSize;
    }

    public Collection<ThreadStatus> getWritingThreads() {
        Collection<ThreadStatus> threadsStatus = new LinkedList<>();
        for (Thread t : writingThreads)
            threadsStatus.add(new ThreadStatus(t));
        return threadsStatus;
    }

    public int getReadersCount() {
        return readersCount;
    }

    public int getWritingThreadIndex() {
        return writingThreadIndex;
    }

    public int getWritingQueueLength() {
        return writingQueueLength;
    }

    //As a REST service to retireve current status and take actions
    @GET
    @Path("status")
    public ResourceMonitor status() {
        return this;
    }

    @POST
    @Path ("unlock")
    public synchronized void unlockThreads() {
        notifyAll();
    }

}
