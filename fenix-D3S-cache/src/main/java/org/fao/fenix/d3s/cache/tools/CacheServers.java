package org.fao.fenix.d3s.cache.tools;


import org.fao.fenix.d3s.cache.storage.Storage;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;


public class CacheServers {
    @Inject private Instance<Storage> storageFactory;
    @Inject private Instance<Server> serverFactory;

    public void startup() throws Exception {
        for (Server server : serverFactory.select(Server.class))
            server.start();
        for (Storage storage : storageFactory.select(Storage.class))
            storage.open();
    }

    public void shutdown() throws Exception {
        for (Storage storage : storageFactory.select(Storage.class))
            storage.close();
        for (Server server : serverFactory.select(Server.class))
            server.stop();
    }
}
