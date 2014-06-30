package org.fao.fenix.d3s.search.bl.aggregation;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.templates.canc.common.ValueOperator;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Iterator;
import java.util.Map;

@Dependent
public class H2Aggregator extends Aggregator {

    @Inject H2AggregationDao dao;
    Map<String,ValueOperator> operatorInfo;

    @Override
    public void init(SearchStep source, ResourceFilter filter, ODocument dataset) throws Exception { //first level aggregation init (single dataset)
        cloneResult(source);
        data = null;
        operatorInfo = findAggregationRules(filter,dataset);

        dao.init(this);
        dao.appendData(source);
    }

    @Override
    public Iterator<Object[]> iterator() {
        try {
            data = dao.aggregate(operatorInfo);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return super.iterator();
    }

}
