package org.fao.fenix.d3s.wds.dataset;


import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.LinkedList;

public class DatasetStructure {

    public DatasetStructure() { }
    public DatasetStructure(MeIdentification<DSDDataset> metadata) {
        DSDDataset dsd = metadata!=null ? metadata.getDsd() : null;
        Collection<DSDColumn> columns = dsd!=null ? dsd.getColumns() : null;
        if (columns!=null && columns.size()>0) {
            StringBuilder queryBuffer = new StringBuilder();
            Collection<Integer> selectIndexes = new LinkedList<>();
            Collection<Integer> keyIndexes = new LinkedList<>();
            Collection<Integer> singleValuesIndexes = new LinkedList<>();
            Collection<DSDColumn> selectColumns = new LinkedList<>();
            Collection<Object> singleValues = new LinkedList<>();
            int columnIndex = 0;
            for (DSDColumn column : columns) {
                if (Boolean.TRUE.equals(column.getVirtual())) {
                    Collection values = column.getValues()!=null && Collection.class.isAssignableFrom(column.getValues().getClass()) ? (Collection)column.getValues() : null;
                    Object value = values != null && values.size() == 1 ? values.iterator().next() : null;
                    if (value != null) {
                        singleValuesIndexes.add(columnIndex++);
                        singleValues.add(value);
                    }
                } else {
                    if (Boolean.TRUE.equals(column.getKey()))
                        keyIndexes.add(columnIndex);
                    selectIndexes.add(columnIndex++);
                    selectColumns.add(column);
                    queryBuffer.append(',').append(column.getId());
                }
            }

            this.columns = columns.toArray(new DSDColumn[columns.size()]);
            this.selectColumnsQueryFields = queryBuffer.substring(1);
            this.selectColumnsIndexes = selectIndexes.toArray(new Integer[selectIndexes.size()]);
            this.keyColumnsIndexes = keyIndexes.toArray(new Integer[keyIndexes.size()]);
            this.selectColumns = selectColumns.toArray(new DSDColumn[selectColumns.size()]);
            this.singleValuesIndexes = singleValuesIndexes.toArray(new Integer[singleValuesIndexes.size()]);
            this.singleValues = singleValues.toArray(new Object[singleValues.size()]);
        }
    }


    public DSDColumn[] columns;

    public Object[] singleValues;
    public Integer[] singleValuesIndexes;

    public DSDColumn[] selectColumns;
    public String selectColumnsQueryFields;
    public Integer[] selectColumnsIndexes;

    public Integer[] keyColumnsIndexes;

}