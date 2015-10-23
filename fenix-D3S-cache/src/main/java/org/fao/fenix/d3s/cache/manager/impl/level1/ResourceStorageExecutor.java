package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.cache.tools.monitor.ResourceMonitor;

import java.sql.Connection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executors;

public abstract class ResourceStorageExecutor implements Runnable {

    protected String id;
    private ResourceMonitor monitor;
    private DatasetStorage storage;
    private ResourceStorageExecutor next;
    public Exception error;
    public boolean done;


    protected ResourceStorageExecutor(DatasetStorage storage, Table structure, ResourceMonitor monitor) {
        this.monitor = monitor;
        this.storage = storage;
        this.id = structure.getTableName();
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
        Connection connection = null;
        try {
            fireBeginSessionEvent(connection = storage.beginSession(id));
            execute();
            fireEndSessionEvent(connection);
        } catch (Exception ex) {
            error = ex;
        } finally {
            try {
                storage.endSession(id);
            } catch (Exception e) {
                if (error==null)
                    error = e;
                else
                    e.printStackTrace();
            }
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


    //Events management
    private Collection<ExecutorListener> listeners = new LinkedList<>();
    public void addListener(ExecutorListener listener) {
        listeners.add(listener);
    }

    private void fireBeginSessionEvent(Connection connection) {
        for (ExecutorListener listener : listeners)
            listener.storeSessionBegin(storage, storage.getTableName(id), connection);
    }
    private void fireEndSessionEvent(Connection connection) {
        for (ExecutorListener listener : listeners)
            listener.storeSessionEnd(storage, storage.getTableName(id), connection);
    }

}
