package org.fao.fenix.d3s.cache.storage.dataset;

import org.fao.fenix.commons.find.dto.filter.DataFilter;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.cache.dto.StoreStatus;
import org.fao.fenix.d3s.cache.dto.dataset.Table;
import org.fao.fenix.d3s.cache.storage.Storage;

import java.sql.Connection;
import java.util.Date;
import java.util.Iterator;

public interface DatasetStorage extends Storage {

    /**
     * Communicate the begin of a store session on a specific table (useful to enhance performance)
     * @param table Target table
     * @throws Exception
     */
    public Connection beginSession(String tableName) throws Exception;

    /**
     * Communicate the end of a store session on a specific table
     * @param table Target table
     * @throws Exception
     */
    public void endSession(String tableName) throws Exception;

    /**
     * Create a new table with the specified structure if it don't exists.
     * Metadata will be updated automatically.
     * @param tableStructure New table metadata
     * @param timeout Table timeout timestamp (useful to clean temporary tables)
     * @throws Exception
     */
    public StoreStatus create(Table tableStructure, Date timeout) throws Exception;

    /**
     * Load data from existing tables. There's no check about data quality or data structure. The execution is synchronous and produced data is volatile.
     * If a source table don't have a column specified in the rows filter the correspondent data will be discarded.
     * The result will contain all the columns specified into the columns filter. If the columns filter is null or empty:
     *   if there's only one valid source table all of the table columns will be included.
     *   if there are more than one valid table, it will be included only columns available in all tables.
     * @param ordering Ordering parameters.
     * @param pagination Pagination parameters.
     * @param filter Rows and columns filter.
     * @param tables Involved source tables metadata.
     * @return Selected data.
     * @throws Exception
     */
    public Iterator<Object[]> load(Order ordering, Page pagination, DataFilter filter, Table ... tables) throws Exception;

    /**
     * Store data into an existing table.
     * If size > 0 the function will consume and store only 'size' rows from data iterator. Else it will consume all available data.
     * If overwrite is true existing data wil be deleted before to start store activity.
     * Table must be present and data have to be syntactically correct considering table structure.
     * Metadata will be updated automatically.
     * @param table Destination table metadata.
     * @param data External data source.
     * @param size Current data block the function should try to consume.
     * @param overwrite Flag to set overwrite or append mode.
     * @return Resource data storage status
     * @throws Exception
     */
    public StoreStatus store(Table table, Iterator<Object[]> data, int size, boolean overwrite, Date referenceDate) throws Exception;

    /**
     * Store data into an existing table loading rows from other tables.
     * Rules about load and store activities are inherited form basic load and store functions.
     * Metadata will be updated automatically.
     * @param table Destination table metadata.
     * @param filter Rows and columns filter.
     * @param overwrite Flag to set overwrite or append mode.
     * @param tables Involved source tables metadata.
     * @return Resource data storage status
     * @throws Exception
     */
    public StoreStatus store(Table table, DataFilter filter, boolean overwrite, Date referenceDate, Table... tables) throws Exception;

    /**
     * Remove an existing table (and related metadata)
     * @param tableName
     * @throws Exception
     */
    public void delete(String tableName) throws Exception;

    /**
     * Returns a new connection to the database. The connection have to be managed by the calling process. The connection has auto-commit mode disabled.
     * @return New JDBC connection.
     * @throws Exception
     */
    public Connection getConnection() throws Exception;

    /**
     * Returns the internal table name associated to a cached resource ID
     * @param resourceId Cached resource ID
     * @return Internal table name
     */
    public String getTableName(String resourceId);

}
