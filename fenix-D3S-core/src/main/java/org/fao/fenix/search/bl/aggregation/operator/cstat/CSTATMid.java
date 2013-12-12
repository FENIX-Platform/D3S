package org.fao.fenix.search.bl.aggregation.operator.cstat;

import org.fao.fenix.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.search.dto.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Component("operator.CSTATMid")
@Scope("prototype")
@OperatorColumns({"FLAG"})
public class CSTATMid extends H2Operator {

    private Double result = 0.0;
    private int count = 0;


    //H2 AGGREGATION
    @Override public void init(Connection connection) throws SQLException { }

    @Override public int getType(int[] ints) throws SQLException { return Types.DOUBLE; }

    @Override
    public void add(Object o) throws SQLException {
//        System.out.println("C_Add_add: v="+o);
        if (result!=null)
            if (o==null)
                result = null;
            else {
                result += (Double)o;
                count++;
            }
    }

    @Override
    public Object getResult() throws SQLException {
//        System.out.println("C_Add_Ris: "+result);
        return result!=null ? result/count : null;
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
