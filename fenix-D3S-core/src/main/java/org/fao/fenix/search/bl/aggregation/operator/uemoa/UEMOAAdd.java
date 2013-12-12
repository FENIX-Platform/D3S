package org.fao.fenix.search.bl.aggregation.operator.uemoa;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.search.bl.aggregation.operator.cstat.RegionValue;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.server.tools.orient.OrientDao;
import org.fao.fenix.server.tools.orient.OrientServer;
import org.fao.fenix.server.utils.DatabaseTimePeriod;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Component("operator.UEMOAAdd")
@Scope("prototype")
@OperatorColumns({"FLAG","GEO","ITEM","TIME"})
public class UEMOAAdd extends RegionValue {

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
