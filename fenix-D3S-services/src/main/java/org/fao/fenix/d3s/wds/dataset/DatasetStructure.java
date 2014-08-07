package org.fao.fenix.d3s.wds.dataset;

import org.fao.fenix.commons.msd.dto.full.DSDColumn;

public class DatasetStructure {

    public DSDColumn[] columns;

    public DSDColumn[] selectColumns;
    public Object[] singleValues;
    public String selectColumnsQueryFields;

    public Integer[] selectColumnsIndexes;
    public Integer[] singleValuesIndexes;

}
