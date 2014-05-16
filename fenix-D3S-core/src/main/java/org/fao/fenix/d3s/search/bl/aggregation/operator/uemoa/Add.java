package org.fao.fenix.d3s.search.bl.aggregation.operator.uemoa;

import org.fao.fenix.d3s.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.d3s.search.bl.aggregation.operator.cstat.RegionValue;

import javax.enterprise.context.Dependent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Dependent
@OperatorColumns({"FLAG","GEO","ITEM","TIME"})
public class Add extends RegionValue {

    //H2 AGGREGATION
    @Override
    public void init(Connection connection) throws SQLException {
        initAggregationsKeys(Region.UEMOA);
        result = 0.0;
    }

    @Override
    public int getType(int[] ints) throws SQLException {
        return Types.DOUBLE;
    }

    @Override
    public Object apply(Object value,Object result) {
        if (result==null || value==null)
            return null;
        return value.equals(Double.MIN_VALUE) ? result : (Double)result + (Double)value;
    }

    @Override
    public Object getResult() throws SQLException {
        return getResult(Region.UEMOA,Boolean.TRUE.equals(getFlow().getBusinessParameters().get("checkAllCountriesKeyProducts")));
    }

    public static Object[] prepareValueForAggregation(Double value, String flag, String geo, String item, Long year) {
        return prepareValueForAggregation(Region.UEMOA,value,flag,geo,item,year);
    }
}
