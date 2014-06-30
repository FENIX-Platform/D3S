package org.fao.fenix.d3s.search.bl.aggregation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.templates.canc.common.ValueOperator;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.bl.aggregation.operator.Operator;
import org.fao.fenix.d3s.search.bl.aggregation.operator.OperatorFactory;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class BasicAggregator extends Aggregator {
    @Inject OperatorFactory operatorFactory;
    Map<String,Operator> defaultRules;


    private String getKey(Object[] row, Integer[] keyColumns) {
        StringBuilder buffer = new StringBuilder(256);
        for (int i=0; i<keyColumns.length; i++)
            buffer.append(row[keyColumns[i]]!=null ? row[keyColumns[i]].hashCode() : -1).append('|');
        return buffer.toString();
    }

    @Override
    public void init(SearchStep source, ResourceFilter filter, ODocument dataset) throws Exception {
        cloneResult(source);
        loadRules(filter, dataset);
        aggregate();
    }

    private void loadRules(ResourceFilter filter, ODocument dataset) throws Exception {
        defaultRules = new HashMap<>();
        for (Map.Entry<String, ValueOperator> operatorInfoEntry : findAggregationRules(filter,null).entrySet())
            defaultRules.put(operatorInfoEntry.getKey(), operatorFactory.getInstance(this, operatorInfoEntry.getValue()));
    }



    private void aggregate() throws Exception {
        //Create rows groups
        Map<String, Collection<Object[]>> groups = new HashMap<String, Collection<Object[]>>();
        for (Object[] row : data) {
            String key = getKey(row,keyIndexes);
            Collection<Object[]> group = groups.get(key);
            if (group==null)
                groups.put(key, group=new LinkedList<Object[]>());
            group.add(row);
        }
        //Aggregate
        //TODO usare la regola di default per le colonne non chiave non incluse nella mappa degli aggregatori
        data = new LinkedList<Object[]>();
        for (Collection<Object[]> group : groups.values()) {
            Object[] row = null;
/*            defaultRule.reset();
            for (Object[] rowI : group)
                defaultRule.evaluate(row=rowI);
            row[valueIndex] = defaultRule.getFinalResult();
*/            ((Collection<Object[]>)data).add(row);
        }
    }
}
