package org.fao.fenix.d3s.wds.dataset;

import org.fao.fenix.commons.msd.dto.full.DSD;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.wds.WDSDao;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class WDSDatasetDao extends WDSDao<Iterator<Object[]>> {

    //Support consumer pattern
    public abstract void consume(Object... args);
    public abstract void consumed(Object... args);

    protected Iterator<Object[]> getConsumerIterator(final Iterator<Object[]> source, final Object... args) {
        return source==null ? null : new Iterator<Object[]>() {
            boolean toConsume = false;

            @Override
            public boolean hasNext() {
                if (source.hasNext()) {
                    if (!toConsume) {
                        toConsume = true;
                        consume(args);
                    }
                    return true;
                } else {
                    if (toConsume) {
                        toConsume = false;
                        consumed(args);
                    }
                    return false;
                }
            }

            @Override
            public Object[] next() {
                return source.next();
            }

            @Override
            public void remove() {
                source.remove();
            }
        };
    }


    //Usage of metadata summary

    protected abstract Iterator<Object[]> loadData (MeIdentification resource, DatasetStructure structure) throws Exception;
    protected abstract void storeData(MeIdentification resource, Iterator<Object[]> data, boolean overwrite, DatasetStructure structure) throws Exception;

    @Override
    public Iterator<Object[]> loadData(MeIdentification resource) throws Exception {
        final DatasetStructure structure = getDatasetStructure(resource);
        final Iterator<Object[]> rawData = loadData(resource,structure);
            return rawData==null | structure==null ? rawData : new Iterator<Object[]>() {
                @Override
                public boolean hasNext() {
                    return rawData.hasNext();
                }
                @Override
                public void remove() {
                    rawData.remove();
                }
                @Override
                public Object[] next() {
                    Object[] rawRow = rawData.next();
                    if (rawRow!=null) {
                        Object[] row = new Object[structure.columns.length];
                        for (int i=0; i<structure.selectColumnsIndexes.length; i++)
                            row[structure.selectColumnsIndexes[i]] = rawRow[i];
                        for (int i=0; i<structure.singleValuesIndexes.length; i++)
                            row[structure.singleValuesIndexes[i]] = structure.singleValues[i];
                        return row;
                    } else
                        return rawRow;
                }
            };
    }
    @Override
    public void storeData(MeIdentification resource, Iterator<Object[]> data, boolean overwrite) throws Exception {
        storeData(resource,data,overwrite,getDatasetStructure(resource));
    }



    //Utils
    private DatasetStructure getDatasetStructure(MeIdentification metadata) {
        if (metadata!=null) {
            DSD dsd = metadata.getDsd();
            if (dsd!=null) {
                DSDColumn[] columns = dsd.getColumns();
                if (columns!=null && columns.length>0) {
                    StringBuilder queryBuffer = new StringBuilder();
                    Collection<Integer> selectIndexes = new LinkedList<>();
                    Collection<Integer> singleValuesIndexes = new LinkedList<>();
                    Collection<DSDColumn> selectColumns = new LinkedList<>();
                    Collection<Object> singleValues = new LinkedList<>();
                    for (int i=0; i<columns.length; i++) {
                        DSDColumn column = columns[i];
                        if (Boolean.TRUE.equals(column.getVirtual())) {
                            Object[] values = column.getValues();
                            Object value = values!=null && values.length==1 ? values[0] : null;
                            if (value!=null) {
                                singleValuesIndexes.add(i);
                                singleValues.add(value);
                            }
                        } else {
                            selectIndexes.add(i);
                            selectColumns.add(column);
                            queryBuffer.append(',').append(column.getId());
                        }
                    }

                    DatasetStructure datasetStructure = new DatasetStructure();
                    datasetStructure.columns = columns;
                    datasetStructure.selectColumnsQueryFields = queryBuffer.substring(1);
                    datasetStructure.selectColumnsIndexes = selectIndexes.toArray(new Integer[selectIndexes.size()]);
                    datasetStructure.selectColumns = selectColumns.toArray(new DSDColumn[selectColumns.size()]);
                    datasetStructure.singleValuesIndexes = singleValuesIndexes.toArray(new Integer[singleValuesIndexes.size()]);
                    datasetStructure.singleValues = singleValues.toArray(new Object[singleValues.size()]);

                    return datasetStructure;
                }
            }
        }
        return null;
    }

}
