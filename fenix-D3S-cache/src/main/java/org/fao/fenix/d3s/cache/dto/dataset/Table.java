package org.fao.fenix.d3s.cache.dto.dataset;

import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.utils.Language;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

public class Table {

    private Collection<Column> columns = new LinkedList<>();
    private String tableName;
    private Set<String> indexes = new HashSet<>();


    public static Table[] getInstances(MeIdentification<DSDDataset> ... metadataArray) {
        Collection<Table> tables = new LinkedList<>();
        if (metadataArray!=null)
            for (MeIdentification<DSDDataset> metadata : metadataArray)
                if (metadata!=null)
                    tables.add(new Table(metadata));

        return tables.size()>0 ? tables.toArray(new Table[tables.size()]) : null;
    }


    public Table() {
        init(null,null,null);
    }
    public Table(String tableName) {
        init(tableName,null,null);
    }

    public Table(String tableName, DSDDataset structure) {
        this(tableName,structure,null);
    }
    public Table(MeIdentification<DSDDataset> metadata) {
        this(metadata,null);
    }
    public Table(String tableName, DSDDataset structure, Language language) {
        init(tableName, structure, language);
    }
    public Table(MeIdentification<DSDDataset> metadata, Language language) {
        init(getTableName(metadata), metadata!=null ? metadata.getDsd() : null, language);
    }

    private void init(String tableName, DSDDataset structure, Language language) {
        this.tableName = tableName;
        setColumns(extend(structure,language));
    }

    private DSDDataset extend (DSDDataset structure, Language language) {
        if (structure!=null) {
            Collection<DSDColumn> columns = new LinkedList<>();
            if (structure.getColumns()!=null) {
                columns.addAll(structure.getColumns());
                if (language!=null)
                    for (DSDColumn column : structure.getColumns())
                        if (column.getDataType()== DataType.code || column.getDataType()==DataType.customCode) {
                            DSDColumn newColumn = new DSDColumn();
                            newColumn.setId(column.getId()+'_'+language.getCode());
                            newColumn.setDataType(DataType.text);
                            newColumn.setTitle(column.getTitle());
                            newColumn.setKey(false);
                            newColumn.setVirtual(false);
                            newColumn.setTransposed(false);
                            columns.add(newColumn);
                        }
            }

            DSDDataset dsd = new DSDDataset();
            dsd.setColumns(columns);
            return dsd;
        } else
            return null;
    }

    protected String getTableName(MeIdentification<DSDDataset> metadata) {
        if (tableName!=null) {
            String tableName = metadata.getUid();
            String version = metadata.getVersion();
            if (version != null)
                tableName += '|' + version;
            return tableName;
        } else
            return null;
    }

    protected void setColumns(DSDDataset structure) {
        Collection<DSDColumn> columnsStructure = structure!=null ? structure.getColumns() : null;
        if (columnsStructure!=null)
            for (DSDColumn column : columnsStructure)
                columns.add(new Column(column));
    }
    /*
    public Table(String tableName, String schema, Connection connection) throws Exception {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        this.tableName = tableName;

        tableName = schema!=null ? schema+'.'+tableName : tableName;

        Set<String> keyColumns = new HashSet<>();
        for (ResultSet column = databaseMetaData.getPrimaryKeys(null, null, tableName); column.next(); )
            keyColumns.add(column.getString("COLUMN_NAME"));
        Map<Integer, Column> columnsMap = new TreeMap<>();
        for (ResultSet column = databaseMetaData.getColumns(null, null, tableName, null); column.next(); )
            columnsMap.put(column.getInt("ORDINAL_POSITION"), new Column(column,keyColumns));
        columns.addAll(columnsMap.values());

        int indexPrefixLength = "IDX_".length() + tableName.length();
        for (ResultSet indexInfo = databaseMetaData.getIndexInfo(null, null, tableName, false, false); indexInfo.next(); )
            indexes.add(indexInfo.getString("INDEX_NAME").substring(indexPrefixLength));
    }
*/


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
