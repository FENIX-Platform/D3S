package org.fao.fenix.d3s.wds.dataset;

import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.LinkedList;

public class DatasetStructure {

    public DatasetStructure() { }
    public DatasetStructure(MeIdentification metadata) {
        DSD dsd = metadata!=null ? metadata.getDsd() : null;
        DSDColumn[] columns = dsd!=null && dsd instanceof DSDDataset ? ((DSDDataset)dsd).getColumns() : null;
        if (columns!=null && columns.length>0) {
            StringBuilder queryBuffer = new StringBuilder();
            Collection<Integer> selectIndexes = new LinkedList<>();
            Collection<Integer> keyIndexes = new LinkedList<>();
            Collection<Integer> singleValuesIndexes = new LinkedList<>();
            Collection<DSDColumn> selectColumns = new LinkedList<>();
            Collection<Object> singleValues = new LinkedList<>();
            for (int i = 0; i < columns.length; i++) {
                DSDColumn column = columns[i];
                if (Boolean.TRUE.equals(column.getVirtual())) {
                    Object[] values = column.getValues();
                    Object value = values != null && values.length == 1 ? values[0] : null;
                    if (value != null) {
                        singleValuesIndexes.add(i);
                        singleValues.add(value);
                    }
                } else {
                    selectIndexes.add(i);
                    if (Boolean.TRUE.equals(column.getKey()))
                        keyIndexes.add(i);
                    selectColumns.add(column);
                    queryBuffer.append(',').append(column.getId());
                }
            }

            this.columns = columns;
            this.selectColumnsQueryFields = queryBuffer.substring(1);
            this.selectColumnsIndexes = selectIndexes.toArray(new Integer[selectIndexes.size()]);
            this.keyColumnsIndexes = keyIndexes.toArray(new Integer[keyIndexes.size()]);
            this.selectColumns = selectColumns.toArray(new DSDColumn[selectColumns.size()]);
            this.singleValuesIndexes = singleValuesIndexes.toArray(new Integer[singleValuesIndexes.size()]);
            this.singleValues = singleValues.toArray(new Object[singleValues.size()]);
        }
    }


    public DSDColumn[] columns;

    public DSDColumn[] selectColumns;
    public Object[] singleValues;
    public String selectColumnsQueryFields;

    public Integer[] selectColumnsIndexes;
    public Integer[] keyColumnsIndexes;
    public Integer[] singleValuesIndexes;

}