package org.fao.fenix.d3s.search.bl.aggregation.operator.standard;

import org.fao.fenix.d3s.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

@Component("operator.NoKey")
@Scope("prototype")
public class NoKey extends H2Operator {


    //STD OPERATOR
    @Override
    protected void init(SearchFilter filter) throws Exception {
    }

    @Override
    public void reset() {
    }

    @Override
    public void evaluate(Object[] row) {
    }

    @Override
    public Number getFinalResult() {
        return null;
    }


    //H2 AGGREGATION
    @Override
    public void init(Connection connection) throws SQLException {
    }

    @Override
    public int getType(int[] ints) throws SQLException {
        return ints!=null && ints.length>0 ? ints[0] : Types.VARCHAR;
    }

    private Object result;
    private boolean start;
    @Override
    public void add(Object o) throws SQLException {
        if(!start) {
            result = o;
            start = true;
        } else if (o==null || !o.equals(result)) {
            result = null;
        }

    }

    @Override
    public Object getResult() throws SQLException {
        start = false;
        return result;
    }

}
