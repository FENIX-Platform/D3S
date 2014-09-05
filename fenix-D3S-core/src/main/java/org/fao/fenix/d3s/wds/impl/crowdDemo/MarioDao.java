package org.fao.fenix.d3s.wds.impl.crowdDemo;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.wds.impl.DBDao;

import javax.enterprise.context.Dependent;
import java.sql.Connection;

@Dependent
public class MarioDao extends DBDao {

    private static String query =
            "select city.name as city, (market.name||' ('||marketcode||')') as market, (vendor.name||' ('||vendorCode||')') as vendor, munit.name as unit, currency.name as currency, (commodity.name||' ('||commoditycode||')') as commodity, (variety.name||' ('||varietycode||')') as variety, date, fulldate as time, price " +
            "from data " +
            "left outer join city on (city.code = data.citycode) " +
            "left outer join market on (market.code = data.marketcode) " +
            "left outer join munit on (munit.code = data.munitcode) " +
            "left outer join commodity on (commodity.code = data.commoditycode) " +
            "left outer join variety on (variety.code = data.varietycode) " +
            "left outer join vendor on (vendor.code = data.vendorCode) " +
            "left outer join currency on (currency.code = data.currencycode)";

    @Override
    public void load(ResourceFilter filter, ODocument dataset) throws Exception {
        Connection connection = null;
        try {
            connection = getConnection("crowdDemo.database");
            setData(connection.createStatement().executeQuery(query));
        } finally {
            if (connection!=null)
                connection.close();
        }
    }

    @Override
    public void store(Iterable<Object[]> data, ODocument dataset) throws Exception {
        throw new UnsupportedOperationException();
    }
}
