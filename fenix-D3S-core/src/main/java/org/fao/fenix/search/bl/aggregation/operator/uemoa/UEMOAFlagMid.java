package org.fao.fenix.search.bl.aggregation.operator.uemoa;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.search.bl.aggregation.operator.cstat.RegionFlag;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.server.tools.orient.OrientDao;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.server.utils.DatabaseTimePeriod;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Component("operator.UEMOAFlagMid")
@Scope("prototype")
@OperatorColumns({"VALUE","GEO","ITEM","TIME"})
public class UEMOAFlagMid extends RegionFlag {

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