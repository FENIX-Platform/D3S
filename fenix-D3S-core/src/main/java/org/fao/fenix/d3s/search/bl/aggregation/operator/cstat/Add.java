package org.fao.fenix.d3s.search.bl.aggregation.operator.cstat;

import org.fao.fenix.d3s.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.d3s.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import javax.enterprise.context.Dependent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Dependent
@OperatorColumns({"FLAG"})
public class Add extends H2Operator {

    private Double result = 0.0;


    //H2 AGGREGATION
    @Override public void init(Connection connection) throws SQLException { }

    @Override public int getType(int[] ints) throws SQLException { return Types.DOUBLE; }

    @Override
    public void add(Object o) throws SQLException {
//        System.out.println("C_Add_add: v="+o);
        if (result!=null)
            result = o!=null ? result + (Double)o : null;
    }

    @Override
    public Object getResult() throws SQLException {
//        System.out.println("C_Add_Ris: "+result);
        return result;
    }


    //H2 PREPARE VALUE FUNCTION

    public static Double prepareValueForAggregation(Double value, String flag) {
        //Apply flag for value normalization
        if (flag!=null) {
            if (value==null || (value!=0.0 && ("0".equals(flag) || "1".equals(flag)))) // apply flags '0' or '-'
                value = 0.0;
            else if (value!=null && ("4".equals(flag) || "5".equals(flag) || "6".equals(flag) || "7".equals(flag))) // apply flags '.' or '..' or '...' or ':'
                value = null;
        }
        //Return value
        return value;
    }






    //STD OPERATOR
    @Override protected void init(SearchFilter filter) throws Exception { }
    @Override public void reset() { }
    @Override public void evaluate(Object[] row) { }
    @Override public Number getFinalResult() { return null; }
}
