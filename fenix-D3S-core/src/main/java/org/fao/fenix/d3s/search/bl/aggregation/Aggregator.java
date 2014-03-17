package org.fao.fenix.d3s.search.bl.aggregation;

import java.util.*;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.msd.dao.common.CommonsConverter;
import org.fao.fenix.commons.msd.dto.common.ValueOperator;
import org.fao.fenix.d3s.search.SearchFlow;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.commons.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class Aggregator extends SearchStep {
    @Autowired CommonsConverter commonsConverter;

    public abstract void init (SearchStep source, ResourceFilter filter, ODocument dataset) throws Exception;

    //Utils
    @SuppressWarnings("unchecked")
    public Map<String,ValueOperator> findAggregationRules(ResourceFilter filter, ODocument dataset) throws Exception {
        Map<String,ValueOperator> operators = new HashMap<String, ValueOperator>();
        SearchFlow flowData = getFlow();

        if (dataset!=null) {
            Collection<ODocument> datasetAggregationRules = dataset.field("dsd.aggregationRules");
            if (datasetAggregationRules!=null)
                for (ODocument datasetAggregationRule : datasetAggregationRules) {
                    String dimension = datasetAggregationRule.field("dimension.name");
                    if (!operators.containsKey(dimension))
                        operators.put(dimension,commonsConverter.toOperator(datasetAggregationRule));
                }
            Map<String,Object> filterAggregationsInfo = (Map<String,Object>)flowData.getBusinessParameter("globalAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
            filterAggregationsInfo = (Map<String,Object>)flowData.getBusinessParameter("firstAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
        } else {
            Map<String,Object> filterAggregationsInfo = (Map<String,Object>)flowData.getBusinessParameter("globalAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
            filterAggregationsInfo = (Map<String,Object>)flowData.getBusinessParameter("secondAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
        }

        return operators;
    }


}
