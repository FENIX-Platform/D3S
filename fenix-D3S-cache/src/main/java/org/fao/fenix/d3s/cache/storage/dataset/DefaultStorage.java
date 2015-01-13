package org.fao.fenix.d3s.cache.storage.dataset;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.utils.Iterator;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Column;
import org.fao.fenix.d3s.cache.dto.dataset.Table;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DefaultStorage extends H2Database {


    //DATA
    @Override
    public void create(Table tableStructure) throws Exception {
        if (tableStructure.getColumns().size()==0)
            throw new Exception("Invalid table structure.");

        //Delete existing table
        String tableName = tableStructure.getTableName();
        if (metadata.containsKey(tableName))
            delete(tableName);

        //Create query
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append(" (");

        Object defaultValue = null;
        for (Column column : tableStructure.getColumns()) {
            query.append(column.getName());

            switch (column.getType()) {
                case bool:      query.append(" BOOLEAN"); break;
                case integer:   query.append(" INT"); break;
                case real:      query.append(" DOUBLE"); break;
                case string:    query.append(" VARCHAR"); break;
                case object:    query.append(" OTHER"); break;
            }

            if ((defaultValue = column.getNoDataValue()) != null) {
                switch (column.getType()) {
                    case bool:
                    case integer:
                    case real:
                        query.append(" DEFAULT ").append(defaultValue); break;
                    case string:  query.append(" DEFAULT ").append('"').append(defaultValue).append('"'); break;
                }
            }

            if (column.isKey())
                query.append(" PRIMARY KEY");

            query.append(", ");
        }
        query.setLength(query.length()-2);

        query.append(')');

        //Execute
        Connection connection = getConnection();
        try {
            connection.createStatement().executeUpdate(query.toString());
        } finally {
            if (connection!=null)
            connection.close();
        }
    }

    @Override
    public void delete(String tableName) throws Exception {
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);

            connection.createStatement().executeUpdate("DELETE FROM Metadata WHERE id=\""+tableName+'"');
            connection.createStatement().executeUpdate("DROP TABLE "+tableName);

            connection.commit();
            metadata.remove(tableName);
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            if (connection!=null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }

    }


    @Override
    public Iterator<Object[]> load(Order ordering, Page pagination, DataFilter filter, String... sourceTablesName) throws Exception {
        return null;
    }

    @Override
    public StoreStatus store(String tableName, Iterator<Object[]> data, int size, boolean overwrite) throws Exception {
        return null;
    }

    @Override
    public StoreStatus store(String tableName, DataFilter filter, boolean overwrite, String... sourceTablesName) throws Exception {
        return null;
    }



    //METADATA
    private Map<String, StoreStatus> metadata;

    @Override
    public Map<String, StoreStatus> loadMetadata() throws Exception {
        if (metadata==null) {
            Connection connection = getConnection();
            try {
                metadata = new HashMap<>();
                ResultSet result = connection.createStatement().executeQuery("SELECT id, status, rowsCount, lastUpdate, timeout FROM Metadata");
                while (result.next())
                    metadata.put(result.getString(1), new StoreStatus(StoreStatus.Status.valueOf(result.getString(2)), result.getInt(3), result.getTimestamp(4), result.getTimestamp(5)));
            } catch (Exception ex) {
                metadata = null;
                throw ex;
            } finally {
                if (connection != null)
                    connection.close();
            }
        }
        return metadata;
    }

    @Override
    public StoreStatus loadMetadata(String resourceId) throws Exception {
        return loadMetadata().get(resourceId);
    }

    @Override
    public synchronized void storeMetadata(String resourceId, StoreStatus status) throws Exception {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement( metadata.put(resourceId,status) != null ?
                    "INSERT INTO Metadata (status, rowsCount, lastUpdate, timeout, id) VALUES (?,?,?,?,?)":
                    "UPDATE Metadata SET status=?, rowsCount=?, lastUpdate=?, timeout=? WHERE id=?"
            );

            statement.setString(1,status.getStatus().name());
            if (status.getCount()!=null)
                statement.setInt(2, status.getCount());
            statement.setTimestamp(3, new Timestamp(status.getLastUpdate().getTime()));
            if (status.getTimeout()!=null)
                statement.setTimestamp(4, new Timestamp(status.getTimeout().getTime()));
            statement.setString(5, resourceId);

            statement.executeUpdate();
        } finally {
            metadata = null; //Force metadata refresh/reset

            if (connection!=null)
                connection.close();
        }
    }

    @Override
    public synchronized void storeMetadata(Map<String, StoreStatus> metadata, boolean overwrite) throws Exception {
        Connection connection = getConnection();
        try {
            Set<String> existingMetadata = loadMetadata().keySet();

            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Metadata (status, rowsCount, lastUpdate, timeout, id) VALUES (?,?,?,?,?)");
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE Metadata SET status=?, rowsCount=?, lastUpdate=?, timeout=? WHERE id=?");
            for (Map.Entry<String,StoreStatus> statusEntry : metadata.entrySet()) {
                StoreStatus status = statusEntry.getValue();

                PreparedStatement statement = existingMetadata.remove(statusEntry.getKey()) ? updateStatement : insertStatement;
                statement.setString(1,status.getStatus().name());
                if (status.getCount()!=null)
                    statement.setInt(2, status.getCount());
                statement.setTimestamp(3, new Timestamp(status.getLastUpdate().getTime()));
                if (status.getTimeout()!=null)
                    statement.setTimestamp(4, new Timestamp(status.getTimeout().getTime()));
                statement.setString(5, statusEntry.getKey());

                statement.addBatch();
            }
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            if (overwrite && existingMetadata.size()>0) {
                StringBuilder deleteQuery = new StringBuilder();
                for (String id : existingMetadata)
                    deleteQuery.append(",\"").append(id).append('"');
                connection.createStatement().executeUpdate("DELETE FROM Metadata WHERE id IN ("+deleteQuery.substring(1)+')');
            }

        } finally {
            this.metadata = null; //Force metadata refresh/reset

            if (connection!=null)
                connection.close();
        }

    }


    //MAINTENANCE
    @Override
    public void clean() throws Exception {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() throws Exception {
        for (String id : loadMetadata().keySet())
            delete(id);
    }
}
