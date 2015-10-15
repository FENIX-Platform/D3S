package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;

public abstract class ResourceStorageExecutor implements Runnable {

    protected String id;
    protected ResourceMonitor monitor;
    private ResourceStorageExecutor next;
    public Exception error;
    public boolean done;


    protected ResourceStorageExecutor(String id, ResourceMonitor monitor) {
        this.id = id;
        this.monitor = monitor;
    }

    public void add(ResourceStorageExecutor executor) {
        if (next!=null)
            next.add(executor);
        else
            next = executor;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {

        try {
            execute();
        } catch (Exception ex) {
            error = ex;
        } finally {
            if (id!=null)
                try {
                    //Unlock resource
                    monitor.check(ResourceMonitor.Operation.stopWrite, id, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }


        done = true;

        if (next!=null)
            next.start();

    }

    protected abstract void execute() throws Exception;
}
