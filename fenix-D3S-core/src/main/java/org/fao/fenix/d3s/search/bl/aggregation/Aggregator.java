package org.fao.fenix.d3s.search.bl.aggregation;

import java.util.*;

import org.fao.fenix.d3s.msd.dao.common.CommonsConverter;
import org.fao.fenix.d3s.msd.dto.common.ValueOperator;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.d3s.server.utils.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class Aggregator extends SearchStep {
    @Autowired
    CommonsConverter commonsConverter;

    public abstract void init (SearchStep source, SearchFilter filter, ODocument dataset) throws Exception;

    //Utils
    @SuppressWarnings("unchecked")
    public Map<String,ValueOperator> findAggregationRules(SearchFilter filter, ODocument dataset) throws Exception {
        Map<String,ValueOperator> operators = new HashMap<String, ValueOperator>();

        if (dataset!=null) {
            Collection<ODocument> datasetAggregationRules = dataset.field("dsd.aggregationRules");
            if (datasetAggregationRules!=null)
                for (ODocument datasetAggregationRule : datasetAggregationRules) {
                    String dimension = datasetAggregationRule.field("dimension.name");
                    if (!operators.containsKey(dimension))
                        operators.put(dimension,commonsConverter.toOperator(datasetAggregationRule));
                }
            Map<String,Object> filterAggregationsInfo = (Map<String,Object>)filter.getParameters().get("globalAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
            filterAggregationsInfo = (Map<String,Object>)filter.getParameters().get("firstAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
        } else {
            Map<String,Object> filterAggregationsInfo = (Map<String,Object>)filter.getParameters().get("globalAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
            filterAggregationsInfo = (Map<String,Object>)filter.getParameters().get("secondAggregation");
            if (filterAggregationsInfo!=null)
                for (Map.Entry<String,Object> operatorMapEntry : filterAggregationsInfo.entrySet())
                    operators.put(operatorMapEntry.getKey(), JSONUtils.convertValue(operatorMapEntry.getValue(),ValueOperator.class) );
        }

        return operators;
    }




/*
    public Iterable<Object[]> aggregate (SearchFilter filter, SearchDataResponse response, ODocument dataset) throws Exception {
        Collection<DSDColumn> columns = response.getDm().getDsd().getColumns();
        DSDColumn[] columnsArray = columns.toArray(new DSDColumn[columns.size()]);
        //Retrieve aggregation operator
        ValueOperator aggregationRule = findAggregationRule(filter, dataset);
        Operator operator = aggregationRule!=null && aggregationRule.getImplementation()!=null ? (Operator)SpringContext.getBean(aggregationRule.getImplementation()) : null;
        if (operator!=null)
            operator.init(filter, columnsArray, aggregationRule);
        //Aggregate
        aggregate(response, filter, columnsArray, operator);
        return null;
    }

    protected abstract void aggregate (SearchDataResponse response, SearchFilter filter, DSDColumn[] columns, Operator rule) throws Exception;
*/

}
