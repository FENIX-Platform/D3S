package org.fao.fenix.search.bl.aggregation.operator.cstat;

import org.fao.fenix.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.search.dto.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Component("operator.CSTATFlagAdd")
@Scope("prototype")
public class CSTATFlagAdd extends H2Operator {

    private String result = null;


    //H2 AGGREGATION
    @Override
    public void init(Connection connection) throws SQLException { }

    @Override
    public int getType(int[] ints) throws SQLException { return Types.VARCHAR; }

    @Override
    public void add(Object o) throws SQLException {
        //System.out.println("C_Flag_add: v="+o);
        String flag = (String)o;
        if (flag!=null)
            if ("5".equals(flag)||"6".equals(flag)) // apply '..' or '...' flags
                result = "5";
            else if ("7".equals(flag) && (result==null || !"5".equals(result))) // apply ':' flag
                result = "7";
            else if ("4".equals(flag) && (result==null || (!"5".equals(result) && !"7".equals(result)))) // apply '.' flag
                result = "4";
            else if ("2".equals(flag) && (result==null || (!"5".equals(result) && !"7".equals(result) && !"4".equals(result)))) // apply 'e' flag
                result = "2";
    }

    @Override
    public Object getResult() throws SQLException {
//        System.out.println("C_Flag_Ris: "+result);
        return result;
    }





    //STD OPERATOR
    @Override protected void init(SearchFilter filter) throws Exception { }
    @Override public void reset() { }
    @Override public void evaluate(Object[] row) { }
    @Override public Number getFinalResult() { return null; }
}
