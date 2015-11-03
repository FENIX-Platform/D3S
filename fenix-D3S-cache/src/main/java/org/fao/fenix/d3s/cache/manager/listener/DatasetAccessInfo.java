package org.fao.fenix.d3s.cache.manager.listener;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;

import java.sql.Connection;

public class DatasetAccessInfo {

    private MeIdentification metadata;
    private DatasetStorage storage;
    private String tableName;
    private Connection connection;


    public DatasetAccessInfo() {
    }
    public DatasetAccessInfo(MeIdentification metadata, DatasetStorage storage, String tableName, Connection connection) {
        this.metadata = metadata;
        this.storage = storage;
        this.tableName = tableName;
        this.connection = connection;
    }


    public MeIdentification getMetadata() {
        return metadata;
    }

    public void setMetadata(MeIdentification metadata) {
        this.metadata = metadata;
    }

    public DatasetStorage getStorage() {
        return storage;
    }

    public void setStorage(DatasetStorage storage) {
        this.storage = storage;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    //Utils

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();

        if(metadata!=null)
            buffer.append("metadata: ").append(metadata.getUid()).append('-').append(metadata.getVersion()).append('\n');
        buffer.append("storage: ").append(storage!=null).append('\n');
        buffer.append("connection: ").append(connection!=null).append('\n');
        buffer.append("table: ").append(tableName).append('\n');

        return buffer.toString();
    }
}
