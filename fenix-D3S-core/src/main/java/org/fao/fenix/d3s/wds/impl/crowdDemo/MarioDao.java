package org.fao.fenix.d3s.wds.impl.crowdDemo;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.wds.impl.DBDao;

import javax.enterprise.context.Dependent;
import java.sql.Connection;

@Dependent
public class MarioDao extends DBDao {

    private static String query =
            "select city.name as city, (market.name||' ('||marketcode||')') as market, vendorCode as vendor, munit.name as unit, currency.name as currency, (commodity.name||' ('||commoditycode||')') as commodity, (variety.name||' ('||varietycode||')') as variety, date, fulldate as time, note, price " +
            "from data, city, market, munit, currency, commodity, variety " +
            "where data.citycode = city.code and marketcode = market.code and munitcode = munit.code and currencycode = currency.code and commoditycode = commodity.code and varietycode = variety.code";

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
