package org.fao.fenix.d3s.cache.storage.dataset;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.utils.database.DataIterator;
import org.fao.fenix.commons.utils.database.DatabaseUtils;
import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Column;
import org.fao.fenix.d3s.cache.dto.dataset.Table;

import javax.inject.Inject;
import java.sql.*;
import java.util.*;
import java.util.Date;

public abstract class DefaultStorage extends H2Database {
    @Inject DatabaseUtils databaseUtils;


    //DATA
    @Override
    public synchronized void create(Table tableStructure) throws Exception {
        String tableName = tableStructure.getTableName();
        if (tableStructure.getColumns().size()==0 || tableName==null)
            throw new Exception("Invalid table structure.");

        //Check if table exists
        if (loadMetadata().containsKey(tableName))
            throw new Exception("Duplicate table error.");

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

        //Execute query and update metadata
        Connection connection = getConnection();
        try {
            storeMetadata(tableName, new StoreStatus(StoreStatus.Status.loading, 0, new Date(), null), connection);
            connection.createStatement().executeUpdate(query.toString());

            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.close();
        }
    }

    @Override
    public synchronized void delete(String tableName) throws Exception {
        Connection connection = getConnection();
        try {
            removeMetadata(tableName, connection);
            connection.createStatement().executeUpdate("DROP TABLE IF EXISTS " + tableName);

            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.close();
        }

    }


    @Override
    public Iterator<Object[]> load(Order ordering, Page pagination, DataFilter filter, Table... tables) throws Exception {
        if (tables!=null && tables.length>0) {
            //Select data
            Collection<ResultSet> data = new LinkedList<>();
            Connection connection = getConnection();
            try {
                for (Table structure : tables)
                    data.add(load(ordering, pagination, filter, structure));
            } catch (Exception ex) {
                if (!connection.isClosed())
                    connection.close();
                throw ex;
            }

            return new DataIterator(data,connection,10000l);
        } else
            return null;
    }

    private ResultSet load(Order ordering, Page pagination, DataFilter filter, Table table) throws Exception {
        //TODO
        //Create query
        StringBuilder querySelect = new StringBuilder();
        Collection<String> columnsName = filter.getColumns();
        if (columnsName!=null && columnsName.size()>0) {
            for (String name : columnsName)
                querySelect.append(name).append(',');
            querySelect.setLength(querySelect.length()-1);
        } else
            querySelect.append('*');


        return null;
    }

    @Override
    public synchronized StoreStatus store(String tableName, Iterator<Object[]> data, int size, boolean overwrite) throws Exception {
        StoreStatus status = loadMetadata(tableName);
        if (status==null)
            throw new Exception("Unavailable table: "+tableName);

        Connection connection = getConnection();
        try {
            //Retrieve table structure
            Table tableMetadata = new Table(tableName, connection);
            Column[] structure = tableMetadata.getColumns().toArray(new Column[tableMetadata.getColumns().size()]);
            int[] columnsType = new int[structure.length];
            for (int i=0; i<structure.length; i++)
                columnsType[i] = structure[i].getType().getSqlType();

            //If overwrite mode is active delete existing data
            if (overwrite)
                connection.createStatement().executeUpdate("DELETE FROM "+tableName);

            //Build query
            StringBuilder query = new StringBuilder("INSERT INTO ").append(tableName).append(" (");

            for (Column column : structure)
                query.append(column.getName()).append(',');
            query.setLength(query.length()-1);

            query.append(") VALUES (");
            for (int count = structure.length; count>0; count--)
                query.append("?,");
            query.setLength(query.length()-1);
            query.append(')');

            //Prepare store session
            PreparedStatement statement = connection.prepareStatement(query.toString());
            size = size>0 ? size : Integer.MAX_VALUE;
            int count = 0;

            //Store data
            for (; count<size && data.hasNext(); count++) {
                Object[] row = data.next();
                for (int i=0; i<columnsType.length; i++)
                    statement.setObject(i+1,row[i],columnsType[i]);
                statement.addBatch();
            }
            statement.executeBatch();

            //Update status
            status.setStatus(data.hasNext() ? StoreStatus.Status.loading : StoreStatus.Status.ready);
            status.setCount(overwrite ? count : status.getCount() + count);
            status.setLastUpdate(new Date());
            storeMetadata(tableName, status, connection);

            //Close transaction
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            //try to set incomplete status
            status.setStatus(StoreStatus.Status.incomplete);
            status.setLastUpdate(new Date());
            storeMetadata(tableName,status);
            //throw error
            throw ex;
        } finally {
            connection.close();
        }

        return status;
    }

    @Override
    public synchronized StoreStatus store(String tableName, DataFilter filter, boolean overwrite, String... sourceTablesName) throws Exception {
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
    public void storeMetadata(String resourceId, StoreStatus status) throws Exception {
        Connection connection = getConnection();
        try {
            storeMetadata(resourceId,status,connection);
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.close();
        }
    }
    private synchronized void storeMetadata(String resourceId, StoreStatus status, Connection connection) throws Exception {
            PreparedStatement statement = connection.prepareStatement( loadMetadata().put(resourceId, status) != null ?
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
    }

    @Override
    public void removeMetadata(String resourceId) throws Exception {
        Connection connection = getConnection();
        try {
            removeMetadata(resourceId, connection);
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.close();
        }

    }
    public synchronized void removeMetadata(String resourceId, Connection connection) throws Exception {
        if (loadMetadata().containsKey(resourceId))
            connection.createStatement().executeUpdate("DELETE FROM Metadata WHERE id=\""+resourceId+'"');
    }

    @Override
    public synchronized void storeMetadata(Map<String, StoreStatus> metadata, boolean overwrite) throws Exception {
        Connection connection = getConnection();
        try {
            Set<String> existingMetadata = loadMetadata().keySet();

            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Metadata (status, rowsCount, lastUpdate, timeout, id) VALUES (?,?,?,?,?)");
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE Metadata SET status=?, rowsCount=?, lastUpdate=?, timeout=? WHERE id=?");
            for (Map.Entry<String, StoreStatus> statusEntry : metadata.entrySet()) {
                StoreStatus status = statusEntry.getValue();

                PreparedStatement statement = existingMetadata.remove(statusEntry.getKey()) ? updateStatement : insertStatement;
                statement.setString(1, status.getStatus().name());
                if (status.getCount() != null)
                    statement.setInt(2, status.getCount());
                statement.setTimestamp(3, new Timestamp(status.getLastUpdate().getTime()));
                if (status.getTimeout() != null)
                    statement.setTimestamp(4, new Timestamp(status.getTimeout().getTime()));
                statement.setString(5, statusEntry.getKey());

                statement.addBatch();
            }
            insertStatement.executeBatch();
            updateStatement.executeBatch();

            if (overwrite && existingMetadata.size() > 0) {
                StringBuilder deleteQuery = new StringBuilder();
                for (String id : existingMetadata)
                    deleteQuery.append(",\"").append(id).append('"');
                connection.createStatement().executeUpdate("DELETE FROM Metadata WHERE id IN (" + deleteQuery.substring(1) + ')');
            }

            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            this.metadata = null; //Force metadata reload
            connection.close();
        }

    }


    //MAINTENANCE
    @Override
    public synchronized void clean() throws Exception {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public synchronized void reset() throws Exception {
        for (String id : loadMetadata().keySet())
            delete(id);
    }
}
