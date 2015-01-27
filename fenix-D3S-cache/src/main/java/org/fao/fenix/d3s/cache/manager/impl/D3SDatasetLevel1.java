package org.fao.fenix.d3s.cache.manager.impl;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.manager.impl.level1.ExternalDatasetExecutor;
import org.fao.fenix.d3s.cache.manager.impl.level1.InternalDatasetExecutor;
import org.fao.fenix.d3s.cache.storage.Storage;
import org.fao.fenix.d3s.cache.storage.dataset.DefaultStorage;
import org.fao.fenix.d3s.cache.tools.ResourceMonitor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;


@ApplicationScoped
public class D3SDatasetLevel1 implements CacheManager<DSDDataset,Object[]> {

    private static final int SOTRE_PAGE_SIZE = 1000;

    @Inject private DatabaseUtils utils;
    @Inject private DefaultStorage storage;
    @Inject private ResourceMonitor monitor;


    @Override
    public void init() throws Exception {
        //Nothing to do here
    }

    @Override
    public int getStoreBufferSize() {
        return SOTRE_PAGE_SIZE;
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    public int clean() throws Exception {
        return storage.clean();
    }

    @Override
    public Iterator<Object[]> load(MeIdentification<DSDDataset> metadata, Order order, Page page) throws Exception {
        String id = getID(metadata);
        int size = page!=null && page.perPage>0 ? page.skip+page.length : 0;
        boolean ordering = order!=null && order.size()>0;
        monitor.check(ResourceMonitor.Operation.startRead, id, size, ordering);
        //Check resource last update
        StoreStatus status = storage.loadMetadata(id);
        if (status!=null) {
            Date cacheLastUpdate = status.getLastUpdate();
            MeMaintenance meMaintenance = metadata.getMeMaintenance();
            SeUpdate seUpdate = meMaintenance!=null ? meMaintenance.getSeUpdate() : null;
            Date lastUpdate = seUpdate.getUpdateDate();
            if (lastUpdate!=null && cacheLastUpdate.before(lastUpdate))
                storage.delete(id);

        }
        return storage.load(order,page,null,new Table(metadata));
    }

    @Override
    public void store(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data, boolean overwrite, Long timeout) throws Exception {
        //Lock resource
        String id = getID(metadata);
        monitor.check(ResourceMonitor.Operation.startWrite, id, 0, false);
        try {
            //Create table if not exists
            StoreStatus status = storage.loadMetadata(id);
            if (status == null)
                status = storage.create(new Table(metadata), timeout != null ? new Date(System.currentTimeMillis() + timeout) : null);
            //Try to skip with incomplete resources in append mode
            if (!overwrite && status.getStatus() == StoreStatus.Status.incomplete)
                try {
                    data.skip(status.getCount());
                } catch (UnsupportedOperationException ex) {
                    overwrite = true; //If skip is unsupported force overwrite mode
                }
            //Store data and unlock resource
            new ExternalDatasetExecutor(storage, monitor, status, id, data, overwrite).start();
        } catch (Exception ex) {
            //Unlock resource
            monitor.check(ResourceMonitor.Operation.stopWrite, id, 0, false);
            //Throw error
            throw ex;
        }
    }



    @Override
    public void remove(MeIdentification<DSDDataset> metadata) throws Exception {
        //Lock resource
        String id = getID(metadata);
        monitor.check(ResourceMonitor.Operation.startWrite, id, 0, false);
        //Delete
        storage.delete(id);
        //Unlock resource
        monitor.check(ResourceMonitor.Operation.stopWrite, id, 0, false);
    }

    @Override
    public StoreStatus status(MeIdentification<DSDDataset> metadata) throws Exception {
        return storage.loadMetadata(getID(metadata));
    }

    @Override
    public Integer size(MeIdentification<DSDDataset> metadata) throws Exception {
        StoreStatus status = storage.loadMetadata(getID(metadata));
        return status!=null ? status.getCount() : null;
    }

    @Override
    public void filter(Resource<DSDDataset, Object[]>[] resources, StandardFilter rowsFilter, MeIdentification<DSDDataset> destination, boolean overwrite, Long timeout) throws Exception {
        //Lock resource
        String id = getID(destination);
        monitor.check(ResourceMonitor.Operation.startWrite, id, 0, false);
        try {
            Table table = new Table(destination);
            //Store external resources and create corresponding Table metadata
            Collection<String> externalIds = new LinkedList<>();
            Collection<Table> tables = new LinkedList<>();
            for (Resource<DSDDataset, Object[]> resource : resources) {
                Table resourceTable = new Table(resource.getMetadata());
                tables.add(resourceTable);
                if (resource.getData()!=null) {
                    externalIds.add(resourceTable.getTableName());
                    store(resource.getMetadata(), utils.getDataIterator(resource.getData()), true, timeout);
                }
            }
            //Wait for external resources store completion
            for (String externalId : externalIds)
                monitor.check(ResourceMonitor.Operation.startRead, id, 0, true);

            //Start tables merge into the destination table
                //Create table if not exists
                StoreStatus status = storage.loadMetadata(id);
                if (status == null)
                    status = storage.create(table, timeout != null ? new Date(System.currentTimeMillis() + timeout) : null);
                //Store data and unlock resource
                DataFilter filter = new DataFilter();
                filter.setRows(rowsFilter);
                for (DSDColumn column : destination.getDsd().getColumns())
                    filter.addColumn(column.getSubject()!=null ? column.getSubject() : column.getId());
                new InternalDatasetExecutor(storage, monitor, id, filter, overwrite, tables.toArray(new Table[tables.size()]));
        } catch (Exception ex) {
            //Unlock resource
            monitor.check(ResourceMonitor.Operation.stopWrite, id, 0, false);
            //Throw error
            throw ex;
        }
    }

    @Override
    public Iterator<Object[]> filter(MeIdentification<DSDDataset>[] resourcesMetadata, DataFilter filter, Order order) throws Exception {
        if (resourcesMetadata!=null && resourcesMetadata.length>0) {
            Table[] tables = new Table[resourcesMetadata.length];
            for (int i=0; i<tables.length; i++)
                tables[i] = new Table(resourcesMetadata[i]);

            return storage.load(order,null,filter,tables);
        } else
            return null;
    }


    @Override
    public String getID(MeIdentification metadata) {
        return metadata!=null ? metadata.getUid() + (metadata.getVersion()!=null ? '|'+metadata.getVersion() : "") : null;
    }

}
