package org.fao.fenix.d3s.cache.storage.dataset;

import org.fao.fenix.commons.find.dto.filter.*;
import org.fao.fenix.commons.utils.database.DataIterator;
import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Column;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.dto.dataset.Type;

import java.sql.*;
import java.util.*;
import java.util.Date;

public abstract class DefaultStorage extends H2Database {

    private static String SCHEMA_NAME = "DATA";


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
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(SCHEMA_NAME).append('.').append(tableName).append(" (");

        for (Column column : tableStructure.getColumns()) {
            query.append(column.getName());

            switch (column.getType()) {
                case bool:      query.append(" BOOLEAN"); break;
                case real:      query.append(" DOUBLE"); break;
                case string:    query.append(" VARCHAR"); break;
                case array:    query.append(" ARRAY"); break;
                case object:    query.append(" OTHER"); break;
                case integer:
                    Integer precision = column.getPrecision();
                    if (precision!=null)
                        query.append(" DECIMAL(").append(precision).append(",0)");
                    else
                        query.append(" BIGINT");
                    break;
            }
/*
            Object defaultValue = null;
            if ((defaultValue = column.getNoDataValue()) != null) {
                switch (column.getType()) {
                    case bool:
                    case integer:
                    case real: query.append(" DEFAULT ").append(defaultValue); break;
                    case string:  query.append(" DEFAULT ").append('"').append(defaultValue).append('"'); break;
                }
            }
*/
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
            Collection<Object[]> defaults = new LinkedList<>();
            Connection connection = getConnection();
            try {
                for (Table structure : tables) {
                    data.add(load(connection, ordering, pagination, filter, structure));
                    defaults.add(structure.getNoDataValues());
                }
            } catch (Exception ex) {
                if (!connection.isClosed())
                    connection.close();
                throw ex;
            }
            //Create and return data iterator
            return new DataIterator(data,connection,10000l,defaults);
        } else
            return null;
    }

    private ResultSet load(Connection connection, Order ordering, Page pagination, DataFilter filter, Table table) throws Exception {
        //Create query
        Collection<Object> params = new LinkedList<>();
        String query = createFilterQuery(ordering, pagination, filter, table, params, false);
        //Execute query
        PreparedStatement statement = connection.prepareStatement(query.toString());
        int i=1;
        for (Object param : params)
            statement.setObject(i++, param);
        //Return data
        return statement.executeQuery();
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
            StringBuilder query = new StringBuilder("INSERT INTO ").append(SCHEMA_NAME).append('.').append(tableName).append(" (");

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
    public synchronized StoreStatus store(String tableName, DataFilter filter, boolean overwrite, Table... tables) throws Exception {
        StoreStatus status = loadMetadata(tableName);
        if (status==null)
            throw new Exception("Unavailable table: "+tableName);

        //Prepare queries
        String[] queries = new String[tables.length];
        Collection<Object>[] params = new Collection[tables.length];
        String[] insertQueriesColumnsSegment = new String[tables.length];

        for (int i=0; i<tables.length; i++) {
            queries[i] = createFilterQuery(null, null, filter, tables[i], params[i] = new LinkedList<>(), true);

            StringBuilder segment = new StringBuilder();
            for (Column column : tables[i].getColumns())
                segment.append(',').append(column.getName());
            insertQueriesColumnsSegment[i] = segment.substring(1);
        }

        //Execute queries to populate destination table
        Connection connection = getConnection();
        try {
            int count = 0;
            //Insert data
            for (int i=0; i<tables.length; i++) {
                //Build insert query
                StringBuilder query = new StringBuilder("INSERT INTO ")
                        .append(SCHEMA_NAME).append('.').append(tableName)
                        .append(" (").append(insertQueriesColumnsSegment[i]).append(") ")
                        .append(queries[i]);
                //Run query
                PreparedStatement statement = connection.prepareStatement(query.toString());
                int columnIndex=1;
                for (Object param : params[i])
                    statement.setObject(columnIndex++, param);
                count += statement.executeUpdate();
            }
            //Update status
            status.setStatus(StoreStatus.Status.ready);
            status.setCount(overwrite ? count : status.getCount() + count);
            status.setLastUpdate(new Date());
            storeMetadata(tableName, status, connection);
            //Commit changes
            connection.commit();
        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.close();
        }

        //Return status
        return status;
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
    public synchronized int clean() throws Exception {
        Connection connection = getConnection();
        int count=0;
        try {
            Set<String> tablesName = new HashSet<>();
            for (ResultSet tables=connection.getMetaData().getTables(null,SCHEMA_NAME.toUpperCase(),null,null); tables.next(); )
                tablesName.add(tables.getString("TABLE_NAME"));
            Map<String,StoreStatus> statusMap = loadMetadata();

            for (Map.Entry<String,StoreStatus> statusEntry : statusMap.entrySet()) {
                StoreStatus status = statusEntry.getValue();
                String tableName = statusEntry.getKey();
                Date timeout = status.getTimeout();

                //Remove incomplete and timed out tables
                if (status.getStatus()==StoreStatus.Status.incomplete || timeout!=null && timeout.compareTo(new Date())>0) {
                    delete(tableName);
                    count++;
                }

                //Remove orphan metadata
                if (!tablesName.contains(tableName)) {
                    removeMetadata(tableName);
                    count++;
                } else
                    tablesName.remove(tableName);
            }

            //Remove orphan tables
            for (String tableName : tablesName) {
                connection.createStatement().executeUpdate("DROP TABLE " + SCHEMA_NAME + '.' + tableName);
                count++;
            }

        } catch (Exception ex) {
            connection.rollback();
            throw ex;
        } finally {
            this.metadata = null; //Force metadata reload
            connection.close();
        }

        return count;
    }

    @Override
    public synchronized void reset() throws Exception {
        for (String id : loadMetadata().keySet())
            delete(id);
    }



    //Utils
    private String createFilterQuery(Order ordering, Page pagination, DataFilter filter, Table table, Collection<Object> params, boolean forUpdate) throws Exception {
        Map<String, Column> columnsByName = table.getColumnsByName();

        StringBuilder query = new StringBuilder("SELECT ");

        //Add select columns
        Collection<String> selectColumns = filter.getColumns();
        if (selectColumns!=null && selectColumns.size()>0) {
            selectColumns.retainAll(columnsByName.keySet()); //use only existing columns
            for (String name : selectColumns)
                query.append(name).append(',');
            query.setLength(query.length()-1);
        } else {
            for (Column column : table.getColumns())
                if (column.getType()==Type.array)
                    query.append("ARRAY_GET (").append(column.getName()).append(",1), ");
                else
                    query.append(column.getName()).append(", ");
            query.setLength(query.length()-2);
        }

        //Add source table
        query.append(" FROM ").append(SCHEMA_NAME).append('.').append(table.getTableName());
        //Add where condition
        StandardFilter rowsFilter = filter.getRows();
        if (rowsFilter!=null && rowsFilter.size()>0) {
            query.append(" WHERE 1=1");
            for (Map.Entry<String, FieldFilter> conditionEntry : rowsFilter.entrySet()) {
                String fieldName = conditionEntry.getKey();
                Column column = columnsByName.get(fieldName);
                FieldFilter fieldFilter = conditionEntry.getValue();

                if (column==null)
                    throw new Exception("Wrong table structure for filter:"+table.getTableName()+'.'+fieldName);

                Type columnType = column.getType();
                if (fieldFilter!=null) {
                    switch (fieldFilter.getFilterType()) {
                        case enumeration:
                            if (columnType!=Type.string)
                                throw new Exception("Wrong table structure for filter:"+table.getTableName()+'.'+fieldName);
                            query.append(" AND ").append(fieldName).append(" IN (");
                            for (String value : fieldFilter.enumeration) {
                                query.append("?,");
                                params.add(value);
                            }
                            query.setCharAt(query.length() - 1, ')');
                            break;
                        case time:
                            if (columnType!=Type.integer)
                                throw new Exception("Wrong table structure for filter:"+table.getTableName()+'.'+fieldName);
                            query.append(" AND (");
                            for (TimeFilter timeFilter : fieldFilter.time) {
                                if (timeFilter.from!=null) {
                                    query.append(fieldName).append(" >= ?");
                                    params.add(timeFilter.getFrom(column.getPrecision()));
                                }
                                if (timeFilter.to!=null) {
                                    if (timeFilter.from!=null)
                                        query.append(" AND ");
                                    query.append(fieldName).append(" <= ?");
                                    params.add(timeFilter.getTo(column.getPrecision()));
                                }
                                query.append(" OR ");
                            }
                            query.setLength(query.length()-4);
                            query.append(')');
                            break;
                        case code:
                            query.append(" AND ");
                            if (columnType==Type.string) {
                                query.append(fieldName).append(" IN (");
                                for (CodesFilter codesFilter : fieldFilter.codes)
                                    for (String code : codesFilter.codes) {
                                        query.append("?,");
                                        params.add(code);
                                    }
                                query.setCharAt(query.length()-1, ')');
                            } else if (columnType==Type.array) {
                                query.append('(');
                                for (CodesFilter codesFilter : fieldFilter.codes)
                                    for (String code : codesFilter.codes) {
                                        query.append("ARRAY_CONTAINS (").append(fieldName).append(", ?) OR ");
                                        params.add(code);
                                    }
                                query.setLength(query.length()-4);
                                query.append(')');
                            } else
                                throw new Exception("Wrong table structure for filter:"+table.getTableName()+'.'+fieldName);
                    }

                }
            }
        }

        //Add ordering
        if (ordering!=null)
            query.append(ordering.toH2SQL());

        //Add pagination
        if (pagination!=null)
            query.append(' ').append(pagination.toH2SQL());

        //Add for update option
        if (forUpdate)
            query.append(" FOR UPDATE");

        //Return query
        return query.toString();
    }

}
