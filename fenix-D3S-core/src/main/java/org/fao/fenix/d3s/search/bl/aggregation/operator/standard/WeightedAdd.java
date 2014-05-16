package org.fao.fenix.d3s.search.bl.aggregation.operator.standard;

import org.fao.fenix.d3s.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.d3s.search.bl.aggregation.operator.Operator;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import javax.enterprise.context.Dependent;

@Dependent
@OperatorColumns("WEIGHT")
public class WeightedAdd extends Operator {
    private double result;
    private Number weight;
    private Integer weightIndex;

    @Override
    public void init(SearchFilter filter) throws Exception {
        weight = (Number)aggregationParametersValue.get("weight");
        weightIndex = columnParametersIndex.get("WEIGHT");
        if (weight==null && weightIndex==null)
            weight = 1;
    }

    @Override
    public void reset() {
        result = 0;
    }

    @Override
    public void evaluate(Object[] row) {
        Number value = (Number)row[valueIndex];
        Number weight = this.weight!=null ? this.weight : (Number)row[weightIndex];
        result += value.doubleValue() * weight.doubleValue();
    }

    @Override
    public Number getFinalResult() {
        return result;
    }
}
