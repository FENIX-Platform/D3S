package org.fao.fenix.d3s.search.bl.aggregation.operator.uemoa;

import org.fao.fenix.d3s.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.d3s.search.bl.aggregation.operator.cstat.RegionFlag;

import javax.enterprise.context.Dependent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Dependent
@OperatorColumns({"VALUE","GEO","ITEM","TIME"})
public class FlagMid extends RegionFlag {

    //H2 AGGREGATION
    @Override
    public void init(Connection connection) throws SQLException {
        initAggregationsKeys(Region.UEMOA);
        result = null;
    }

    @Override
    public int getType(int[] ints) throws SQLException {
        return Types.VARCHAR;
    }

    @Override
    public Object apply(Object value,Object result) {
        //flag '..' to apply everytime
        //flag "e" to apply only if it is not '..'
        return value!=null && ("5".equals(value) || result==null) ? value : result;
    }

    @Override
    public Object getResult() throws SQLException {
        return getResult(Region.UEMOA,Boolean.TRUE.equals(getFlow().getBusinessParameters().get("checkAllCountriesKeyProducts")));
    }


    //H2 PREPARE VALUE FUNCTION
    public static Object[] prepareValueForAggregation(String flag, Double value, String geo, String item, Long year) {
        return prepareValueForAggregation(Region.UEMOA,flag,value,geo,item,year);
    }

}
