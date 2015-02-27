package org.fao.fenix.d3s.wds.dataset;

import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.dataset.MetadataDSD;
import org.fao.fenix.d3s.wds.WDSDao;

import java.util.Iterator;

public abstract class WDSDatasetDao extends WDSDao<Iterator<Object[]>> {
    //TODO remove consumer pattern by creating an Orient Data Iterator that can close connection
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

    protected abstract Iterator<Object[]> loadData (MetadataDSD resource, DatasetStructure structure) throws Exception;
    protected abstract void storeData(MetadataDSD resource, Iterator<Object[]> data, boolean overwrite, DatasetStructure structure) throws Exception;
    protected abstract void deleteData(MetadataDSD resource) throws Exception;


    @Override
    public Iterator<Object[]> loadData(org.fao.fenix.commons.msd.dto.full.MeIdentification resource) throws Exception {
        MetadataDSD metadata = ResponseBeanFactory.getInstance(resource, MetadataDSD.class);
        final DatasetStructure structure = new DatasetStructure(metadata);
        final Iterator<Object[]> rawData = loadData(metadata,structure);
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
                        return null;
                }
            };
    }

    @Override
    public void storeData(org.fao.fenix.commons.msd.dto.full.MeIdentification resource, Iterator<Object[]> data, boolean overwrite) throws Exception {
        MetadataDSD metadata = ResponseBeanFactory.getInstance(resource, MetadataDSD.class);
        storeData(metadata,data,overwrite,new DatasetStructure(metadata));
    }

    @Override
    public void deleteData(org.fao.fenix.commons.msd.dto.full.MeIdentification resource) throws Exception {
        MetadataDSD metadata = ResponseBeanFactory.getInstance(resource, MetadataDSD.class);
        deleteData(metadata);
    }


}
