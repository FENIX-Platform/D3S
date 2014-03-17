package org.fao.fenix.d3s.search.bl.aggregation;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class OrientAggregator extends Aggregator {

    @Override
    public void init(SearchStep source, ResourceFilter filter, ODocument dataset) throws Exception {
        //TODO change body of implemented methods use File | Settings | File Templates.
    }
}
