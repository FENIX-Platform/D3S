package org.fao.fenix.d3s.cache.manager;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;

import java.sql.Connection;

public interface CacheListener {

    boolean enable(MeIdentification metadata, DatasetStorage storage, String tableName);

    void created(String tableName, Connection connection);
    void updating(String tableName, Connection connection);
    void updated(String tableName, Connection connection);
    void removing(String tableName);
    void removed(String tableName);
}
