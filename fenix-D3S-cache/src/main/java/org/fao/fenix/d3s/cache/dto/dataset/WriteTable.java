package org.fao.fenix.d3s.cache.dto.dataset;

import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.utils.Language;

import java.util.*;

public class WriteTable extends Table {


    public static WriteTable[] getInstances(MeIdentification<DSDDataset> ... metadataArray) {
        Collection<WriteTable> tables = new LinkedList<>();
        if (metadataArray!=null)
            for (MeIdentification<DSDDataset> metadata : metadataArray)
                if (metadata!=null)
                    tables.add(new WriteTable(metadata));

        return tables.size()>0 ? tables.toArray(new WriteTable[tables.size()]) : null;
    }


    public WriteTable() {
        super();
    }
    public WriteTable(String tableName) {
        super(tableName);
    }

    public WriteTable(String tableName, DSDDataset structure) {
        super(tableName);
        setColumns(extend(structure));
    }
    public WriteTable(MeIdentification<DSDDataset> metadata) {
        if (metadata!=null) {
            setTableName(metadata);
            setColumns(extend(metadata.getDsd()));
        }
    }


    private DSDDataset extend (DSDDataset structure) {
        if (structure!=null) {
            Collection<DSDColumn> columns = new LinkedList<>();
            if (structure.getColumns()!=null) {
                columns.addAll(structure.getColumns());
                for (DSDColumn column : structure.getColumns())
                    if (column.getDataType()==DataType.code || column.getDataType()==DataType.customCode) {
                        for (Language language : Language.values()) {
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
            }

            DSDDataset dsd = new DSDDataset();
            dsd.setColumns(columns);
            return dsd;
        } else
            return null;
    }





}
