package org.fao.fenix.d3s.cache.tools.monitor;

import org.fao.fenix.commons.utils.database.Iterator;

import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MonitorIterator implements Iterator<Object[]>, Runnable {
    public static final long DEFAULT_TIMEOUT_SECS = 30;

    @Inject private ResourceMonitor monitor;
    boolean closed = false, timeout = false;

    private String resourceId;
    private java.util.Iterator<Object[]> source;
    private ScheduledExecutorService timeoutExecutor;
    private long lastActionTime;

    public void init(String resourceId, java.util.Iterator<Object[]> source, boolean timeout) {
        this.resourceId = resourceId;
        this.source = source;

        if (timeout)
            (timeoutExecutor = Executors.newSingleThreadScheduledExecutor()).scheduleWithFixedDelay(this, DEFAULT_TIMEOUT_SECS, DEFAULT_TIMEOUT_SECS, TimeUnit.SECONDS);
    }



    @Override
    public boolean hasNext() {
        if (timeout)
            throw new RuntimeException(new InterruptedException());
        lastActionTime = System.currentTimeMillis();

        boolean hasNext = source.hasNext();
        if (!hasNext)
            close();

        return hasNext;
    }

    @Override
    public Object[] next() {
        if (timeout)
            throw new RuntimeException(new InterruptedException());
        lastActionTime = System.currentTimeMillis();

        Object[] next = source.next();
        if (next==null)
            close();

        return next;
    }

    @Override
    public void remove() {
        if (timeout)
            throw new RuntimeException(new InterruptedException());
        lastActionTime = System.currentTimeMillis();

        source.remove();
    }

    @Override
    public void skip(long amount) {
        if (timeout)
            throw new RuntimeException(new InterruptedException());
        lastActionTime = System.currentTimeMillis();

        if (source instanceof Iterator)
            ((Iterator)source).skip(amount);
        else
            throw new UnsupportedOperationException();
    }

    @Override
    public long getIndex() {
        if (timeout)
            throw new RuntimeException(new InterruptedException());
        lastActionTime = System.currentTimeMillis();

        if (source instanceof Iterator)
            return ((Iterator)source).getIndex();
        else
            throw new UnsupportedOperationException();
    }



    @Override
    public void run() {
        if ( (System.currentTimeMillis()-lastActionTime) > (DEFAULT_TIMEOUT_SECS*1000) ) {
            close();
            timeout = true;
            timeoutExecutor.shutdown();
        }
    }



    private synchronized void close() {
        if (!closed)
            try {
                monitor.check(ResourceMonitor.Operation.stopRead, resourceId, 0);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } finally {
                closed = true;
            }
    }

}
