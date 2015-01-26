package org.fao.fenix.d3s.cache.manager.impl;

import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.storage.dataset.DefaultStorage;
import org.fao.fenix.d3s.cache.tools.ResourceMonitor;



public class D3SDatasetLevel1StoreExecutor implements Runnable {

    private static final int SOTRE_PAGE_SIZE = 1000;

    private DefaultStorage storage;
    private ResourceMonitor monitor;
    private StoreStatus status;
    private String id;
    private Iterator<Object[]> data;
    private boolean overwrite;

    public Exception error;
    public boolean done;


    public D3SDatasetLevel1StoreExecutor(DefaultStorage storage, ResourceMonitor monitor, StoreStatus status, String id, Iterator<Object[]> data, boolean overwrite) {
        this.storage = storage;
        this.monitor = monitor;
        this.status = status;
        this.id = id;
        this.data = data;
        this.overwrite = overwrite;
    }

    public void start() {
        new Thread(this).start();
    }



    @Override
    public void run() {
        try {

            for (status = storage.store(id, data, SOTRE_PAGE_SIZE, overwrite); status.getStatus() == StoreStatus.Status.loading; status = storage.store(id, data, SOTRE_PAGE_SIZE, false))
                monitor.check(ResourceMonitor.Operation.stepWrite, id, status.getCount(), false);

        } catch (Exception ex) {
            error = ex;
        } finally {
            try {
                //Unlock resource
                monitor.check(ResourceMonitor.Operation.stopWrite, id, status.getCount(), false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        done = true;
    }
}
