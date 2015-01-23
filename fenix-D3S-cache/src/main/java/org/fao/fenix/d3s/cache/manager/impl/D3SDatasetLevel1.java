package org.fao.fenix.d3s.cache.manager.impl;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.find.dto.filter.StandardFilter;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.storage.dataset.DefaultStorage;
import org.fao.fenix.d3s.cache.tools.ResourceMonitor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class D3SDatasetLevel1 implements CacheManager<DSDDataset,Object[]> {

    private static final int SOTRE_PAGE_SIZE = 1000;

    private boolean initialized = false;
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
    public int clean() throws Exception {
        return storage.clean();
    }

    @Override
    public Iterator<Object[]> load(MeIdentification<DSDDataset> metadata, Order order, Page page) throws Exception {
        String id = getID(metadata);
        int size = page!=null && page.perPage>0 ? page.skip+page.length : 0;
        boolean ordering = order!=null && order.size()>0;
        monitor.check(ResourceMonitor.Operation.startRead, id, size, ordering);

        return storage.load(order,page,null,new Table(metadata));
    }

    @Override
    public void store(MeIdentification<DSDDataset> metadata, Iterator<Object[]> data, boolean overwrite, Long timeout) throws Exception {
        //TODO verificare il timeout per lo status (dove passarlo)
        //Lock resource
        String id = getID(metadata);
        monitor.check(ResourceMonitor.Operation.startWrite, id, 0, false);
        //Crerate table if not exists
        StoreStatus status = storage.loadMetadata(id);
        if (status==null)
            status = storage.create(new Table(metadata));
        //Try to skip with incomplete resources
        if (status.getStatus() == StoreStatus.Status.incomplete)
            try { data.skip(status.getCount()); } catch (UnsupportedOperationException ex) {}
        //store data
        try {
            for (status = storage.store(id, data, SOTRE_PAGE_SIZE, overwrite); status.getStatus() == StoreStatus.Status.loading; status = storage.store(id, data, SOTRE_PAGE_SIZE, false))
                monitor.check(ResourceMonitor.Operation.stepWrite, id, status.getCount(), false);
        } catch (Exception ex) {
            monitor.check(ResourceMonitor.Operation.stopWrite, id, status.getCount(), false);
            throw ex;
        }
        monitor.check(ResourceMonitor.Operation.stopWrite, id, status.getCount(), false);
    }

    @Override
    public void remove(MeIdentification<DSDDataset> metadata) throws Exception {

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

    }

    @Override
    public Iterator<Object[]> filter(MeIdentification<DSDDataset>[] resourcesMetadata, DataFilter filter, Order order) throws Exception {
        return null;
    }



    //Utils
    private String getID(MeIdentification metadata) {
        return metadata!=null ? metadata.getUid() + (metadata.getVersion()!=null ? '|'+metadata.getVersion() : "") : null;
    }

    /*
    Appunti:
    La funzione load è sincrona. Ritorna null se la risorsa non esiste e un iteratore vuoto se non ci sono dati nella selezione
    Le funzioni filtro sono sincrone. La prima crea una nuova risorsa la seconda consente il caricamento

    Il monitor delle scritture/letture delle risorse deve essere solido.
     Dopo ogni blocco scritto (di 1000 righe) si smazzano tutte le letture possibili e si rimettono le altre in pausa.
     Terminata la scrittura, le letture che non possono essere soddisfatte (pagina inesistente) ritornano 0 righe
    Devo ricordarmi di ritornare errore da tutte le letture in attesa di una scrittura
     */
}
