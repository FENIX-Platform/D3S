package org.fao.fenix.d3s.search.bl.aggregation;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.d3s.msd.dto.common.ValueOperator;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
@Scope("prototype")
public class H2Aggregator extends Aggregator {

    @Autowired H2AggregationDao dao;
    Map<String,ValueOperator> operatorInfo;

    @Override
    public void init(SearchStep source, SearchFilter filter, ODocument dataset) throws Exception { //first level aggregation init (single dataset)
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
