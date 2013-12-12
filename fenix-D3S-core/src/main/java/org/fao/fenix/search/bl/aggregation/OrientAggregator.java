package org.fao.fenix.search.bl.aggregation;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.bl.aggregation.operator.Operator;
import org.fao.fenix.search.dto.SearchDataResponse;
import org.fao.fenix.search.dto.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class OrientAggregator extends Aggregator {

    @Override
    public void init(SearchStep source, SearchFilter filter, ODocument dataset) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
