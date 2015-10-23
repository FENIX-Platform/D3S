package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;

import java.sql.Connection;

public interface ExecutorListener {

    void storeSessionBegin(DatasetStorage storage, String tableName, Connection connection);
    void storeSessionEnd(DatasetStorage storage, String tableName, Connection connection);

}
