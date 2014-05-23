package org.fao.fenix.d3s.search.bl.aggregation.operator.cstat;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.bl.aggregation.operator.H2Operator;
import org.fao.fenix.d3s.search.bl.aggregation.operator.OperatorColumns;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.commons.utils.TimeNumberPeriod;

import java.sql.SQLException;
import java.util.*;

@OperatorColumns({"FLAG","GEO","ITEM","TIME"})
public abstract class RegionAggregation extends H2Operator implements Runnable {

    public enum Region {
        UEMOA("143252"), EAC("143249"), SADC("143251"), ECO("143250");

        Region(String gaul) { gaulCode = gaul; }
        private String gaulCode;
        public String getGaulCode() { return gaulCode; }
    };

    protected static final String[] UEMOA_NATIONS_GAUL = new String[]{
            "42", // - Burkina
            "66", // - Cate d'Ivoire
            "29", // - Benin
            "105", // - Guinea-Bissau
            "181", // - Niger
            "155", // - Mali
            "243", // - Togo
            "218" // - Senkaku Islands
    };
    protected static final String[] EAC_NATIONS_GAUL = new String[]{
            "43", //	Burundi
            "61013", //	Ilemi triangle
            "133", //	Kenya
            "205", //	Rwanda
            "253", //	Uganda
            "257" //	United Republic of Tanzania
    };
    protected static final String[] SADC_NATIONS_GAUL = new String[]{
            "8", //	Angola
            "35", //	Botswana
            "68", //	Democratic Republic of the Congo
            "142", //	Lesotho
            "150", //	Madagascar
            "152", //	Malawi
            "160", //	Mauritius
            "170", //	Mozambique
            "172", //	Namibia
            "220", //	Seychelles
            "227", //	South Africa
            "235", //	Swaziland
            "257", //	United Republic of Tanzania
            "270", //	Zambia
            "271"  //	Zimbabwe
    };
    protected static final String[] ECO_NATIONS_GAUL = new String[]{
            "1", //	Afghanistan
            "19", //	Azerbaijan
            "117", //	Iran  (Islamic Republic of)
            "40781", //	Jammu and Kashmir
            "132", //	Kazakhstan
            "138", //	Kyrgyzstan
            "188", //	Pakistan
            "239", //	Tajikistan
            "249", //	Turkey
            "250", //	Turkmenistan
            "261" //	Uzbekistan
    };
    protected static Map<String,Map<String,TreeSet<TimeNumberPeriod>>> aggregationsKeysUEMOA;
    protected static Map<String,Map<String,TreeSet<TimeNumberPeriod>>> aggregationsKeysEAC;
    protected static Map<String,Map<String,TreeSet<TimeNumberPeriod>>> aggregationsKeysSADC;
    protected static Map<String,Map<String,TreeSet<TimeNumberPeriod>>> aggregationsKeysECO;
    //protected static Date lastUpdate;

    protected Object result;
    protected Set<String> geos = new HashSet<String>();
    protected String item;
    protected Long year;


    //ASYNCH INITIALIZATION

    private Region region;
    private SQLException initAggregationKeysError;
    protected void initAggregationsKeys(Region region) throws SQLException {
        this.region = region;
        Thread initThread = new Thread(this);
        initThread.start();
        try {
            initThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (initAggregationKeysError!=null)
            throw initAggregationKeysError;
    }
    @Override
    public void run() {
        try {
            initAggregationsKeys();
        } catch (SQLException ex) {
            initAggregationKeysError = ex;
        }
    }

    protected void initAggregationsKeys() throws SQLException {
        OGraphDatabase database = OrientServer.getInstance().getDatabase(
                SearchStep.initProperties.getProperty("CountrySTAT.aggregations.keyProductsDatabase.url"),
                SearchStep.initProperties.getProperty("CountrySTAT.aggregations.keyProductsDatabase.usr"),
                SearchStep.initProperties.getProperty("CountrySTAT.aggregations.keyProductsDatabase.psw")
        );
        Map<String,Map<String,TreeSet<TimeNumberPeriod>>> aggregationsKeys = null;
        switch (region) {
            case UEMOA: aggregationsKeys = aggregationsKeysUEMOA; break;
            case EAC: aggregationsKeys = aggregationsKeysEAC; break;
            case SADC: aggregationsKeys = aggregationsKeysSADC; break;
            case ECO: aggregationsKeys = aggregationsKeysECO; break;
        }
        try {
            //Date currentLastUpdate = commonsDao.lastUpdate("aggregationsKey");
            //if (aggregationsKeys==null || (currentLastUpdate!=null && (lastUpdate==null || currentLastUpdate.compareTo(lastUpdate)>0))) {
            if (aggregationsKeys==null) {
                //Load data (switch database to CountrySTAT Orient database)
                aggregationsKeys = new HashMap<String, Map<String, TreeSet<TimeNumberPeriod>>>();
                Collection<ODocument> aggregationsKeyO = loadAggregationsKeyO(region.name(),null,null,null,null,database);
                for (ODocument rowO : aggregationsKeyO) {
                    String geo = rowO.field("GEO.code");
                    String item = rowO.field("ITEM.code");

                    Map<String,TreeSet<TimeNumberPeriod>> aggregationsItemsKeys = aggregationsKeys.get(geo);
                    if (aggregationsItemsKeys==null)
                        aggregationsKeys.put(geo, aggregationsItemsKeys = new HashMap<String, TreeSet<TimeNumberPeriod>>());
                    TreeSet<TimeNumberPeriod> aggregationsPeriodsKeys = aggregationsItemsKeys.get(item);
                    if (aggregationsPeriodsKeys==null)
                        aggregationsItemsKeys.put(item, aggregationsPeriodsKeys = new TreeSet<TimeNumberPeriod>());
                    aggregationsPeriodsKeys.add(new TimeNumberPeriod((Long)rowO.field("TIME_FROM"),(Long)rowO.field("TIME_TO")));
                }
                switch (region) {
                    case UEMOA: aggregationsKeysUEMOA = aggregationsKeys; break;
                    case EAC: aggregationsKeysEAC = aggregationsKeys; break;
                    case SADC: aggregationsKeysSADC = aggregationsKeys; break;
                    case ECO: aggregationsKeysECO = aggregationsKeys; break;
                }
                //lastUpdate = currentLastUpdate;
            }
        } catch (Exception ex) {
            throw new SQLException("UEMOA Key products reading exception: "+ex.getMessage());
        } finally {
            if (database!=null)
                database.close();
        }
    }

    protected abstract Object apply(Object value, Object result);


    @Override
    public void add(Object o) throws SQLException {
        Object[] value = (Object[])o;
        //update data for final check
        geos.add((String)value[1]);
        if (item==null) item = (String)value[2];
        if (year==null) year = (Long)value[3];
        //update result
        result = apply(value[0], result);
    }



    protected static boolean isKeyProduct(Region region, String geo, String item, Long year) {
        boolean key = false;
        Map<String,TreeSet<TimeNumberPeriod>> aggregationsItemsKeys = null;
        switch (region) {
            case UEMOA: aggregationsItemsKeys = aggregationsKeysUEMOA.get(geo); break;
            case EAC: aggregationsItemsKeys = aggregationsKeysEAC.get(geo); break;
            case SADC: aggregationsItemsKeys = aggregationsKeysSADC.get(geo); break;
            case ECO: aggregationsItemsKeys = aggregationsKeysECO.get(geo); break;
        }
        if (aggregationsItemsKeys!=null) {
            TreeSet<TimeNumberPeriod> aggregationsPeriodsKeys = aggregationsItemsKeys.get(item);
            if (aggregationsPeriodsKeys!=null)
                return aggregationsPeriodsKeys.contains(new TimeNumberPeriod(year, year));
        }
        return false;
    }



    //CountrySTAT database utils
    public Collection<ODocument> loadAggregationsKeyO(String region, Code geo, Code item, Long timeFrom, Long timeTo, OGraphDatabase database) throws Exception {
        StringBuilder query = new StringBuilder("SELECT FROM AggregationKeyProducts WHERE region = ?");

        Collection<Object> parameters = new LinkedList<Object>();
        parameters.add(region);
        if (geo!=null) {
            parameters.add(loadCodeO(geo,database));
            query.append(" AND GEO = ?");
        }
        if (item!=null) {
            parameters.add(loadCodeO(item,database));
            query.append(" AND ITEM = ?");
        }
        if (timeFrom!=null) {
            parameters.add(timeFrom);
            query.append(" AND TIME_TO >= ?");
        }
        if (timeTo!=null) {
            parameters.add(timeTo);
            query.append(" AND TIME_FROM <= ?");
        }

        OSQLSynchQuery<ODocument> queryLoadAggregationsKeyByGeo = new OSQLSynchQuery<ODocument>(query.toString());
        return (Collection<ODocument>) database.query(queryLoadAggregationsKeyByGeo, parameters.toArray());
    }

    private static OSQLSynchQuery<ODocument> queryloadCode = new OSQLSynchQuery<ODocument>("select from MSDCode where systemKey = ? and systemVersion = ? and code = ?");
    public synchronized ODocument loadCodeO(Code code, OGraphDatabase database) throws Exception {
        queryloadCode.reset();
        queryloadCode.resetPagination();
        List<ODocument> result = database.query(queryloadCode, code.getSystemKey(), code.getSystemVersion(), code.getCode());
        return result.size()==1 ? result.get(0) : null;
    }




    //STD OPERATOR
    @Override protected void init(SearchFilter filter) throws Exception { }
    @Override public void reset() { }
    @Override public void evaluate(Object[] row) { }
    @Override public Number getFinalResult() { return null; }

}
