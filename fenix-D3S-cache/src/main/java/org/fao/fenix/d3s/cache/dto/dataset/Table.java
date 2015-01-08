package org.fao.fenix.d3s.cache.dto.dataset;

import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.LinkedList;

public class Table {

    private Collection<Column> columns = new LinkedList<>();
    private String tableName;

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
}
