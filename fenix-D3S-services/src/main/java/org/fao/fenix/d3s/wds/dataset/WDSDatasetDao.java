package org.fao.fenix.d3s.wds.dataset;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.dataset.MetadataDSD;
import org.fao.fenix.d3s.wds.WDSDao;

import java.util.Iterator;

public abstract class WDSDatasetDao extends WDSDao<Iterator<Object[]>> {

    protected DatasetStructure getStructure(MeIdentification resource) {
        return new DatasetStructure(resource);
    }

    protected Iterator<Object[]> getVirtualColumnIterator(MeIdentification resource, final Iterator<Object[]> rawData) {
        final DatasetStructure structure = new DatasetStructure(resource);
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
}
