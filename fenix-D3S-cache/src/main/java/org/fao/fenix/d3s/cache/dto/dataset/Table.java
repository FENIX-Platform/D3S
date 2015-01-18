package org.fao.fenix.d3s.cache.dto.dataset;

import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

public class Table {

    private Collection<Column> columns = new LinkedList<>();
    private String tableName;


    public static Table[] getInstances(MeIdentification<DSDDataset> ... metadataArray) {
        Collection<Table> tables = new LinkedList<>();
        if (metadataArray!=null)
            for (MeIdentification<DSDDataset> metadata : metadataArray)
                if (metadata!=null)
                    tables.add(new Table(metadata));

        return tables.size()>0 ? tables.toArray(new Table[tables.size()]) : null;
    }


    public Table() {
    }
    public Table(String tableName) {
        this.tableName = tableName;
    }

    public Table(String tableName, DSDDataset structure) {
        this.tableName = tableName;
        setColumns(structure);
    }
    public Table(MeIdentification<DSDDataset> metadata) {
        if (metadata!=null) {
            tableName = metadata.getUid();
            String version = metadata.getVersion();
            if (version!=null)
                tableName += '|'+version;
        }
        setColumns(metadata.getDsd());
    }

    private void setColumns(DSDDataset structure) {
        Collection<DSDColumn> columnsStructure = structure!=null ? structure.getColumns() : null;
        if (columnsStructure!=null)
            for (DSDColumn column : columnsStructure)
                columns.add(new Column(column));
    }

    public Table(String tableName, Connection connection) throws Exception {
        this.tableName = tableName;

        Set<String> keyColumns = new HashSet<>();
        for (ResultSet column = connection.getMetaData().getPrimaryKeys(null,null,tableName); column.next(); )
            keyColumns.add(column.getString("COLUMN_NAME"));
        Map<Integer, Column> columnsMap = new TreeMap<>();
        for (ResultSet column = connection.getMetaData().getColumns(null,null,tableName,null); column.next(); )
            columnsMap.put(column.getInt("ORDINAL_POSITION"), new Column(column,keyColumns));
        columns.addAll(columnsMap.values());
    }







    public Collection<Column> getColumns() {
        return columns;
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }



    //Utils
    public Set<String> getColumnsName() {
        Set<String> names = new LinkedHashSet<>();
        for (Column column : columns)
            names.add(column.getName());
        return names;
    }

    public Map<String, Column> getColumnsByName() {
        Map<String, Column> columnsByName = new LinkedHashMap<>();
        for (Column column : columns)
            columnsByName.put(column.getName(), column);
        return columnsByName;
    }

    public Object[] getNoDataValues() {
        Object[] noDataValues = new Object[columns.size()];
        Iterator<Column> columnIterator = columns.iterator();
        boolean isNull = true;
        for (int i=0; i<noDataValues.length; i++)
            isNull &= (noDataValues[i]=columnIterator.next().getNoDataValue()) == null;
        return isNull ? null : noDataValues;
    }
}
