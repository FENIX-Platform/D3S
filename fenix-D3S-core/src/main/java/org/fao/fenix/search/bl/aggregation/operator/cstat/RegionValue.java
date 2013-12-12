package org.fao.fenix.search.bl.aggregation.operator.cstat;

import org.fao.fenix.server.utils.DatabaseTimePeriod;

import java.sql.SQLException;
import java.util.*;

public abstract class RegionValue extends RegionAggregation {

    public Object getResult(Region region, boolean allCountries) throws SQLException {
        if (result!=null && allCountries)
            updateWithNoData(region);
        return result;
    }

    protected void updateWithNoData(Region region) {
        String[] regionNations = null;
        switch (region) {
            case UEMOA: regionNations = UEMOA_NATIONS_GAUL; break;
            case EAC: regionNations = EAC_NATIONS_GAUL; break;
            case SADC: regionNations = SADC_NATIONS_GAUL; break;
            case ECO: regionNations = ECO_NATIONS_GAUL; break;
        }
        if (geos.size() < regionNations.length)
            for (String geo : regionNations)
                if (!geos.contains(geo))
                    result = apply(prepareValueForAggregation(region, null,null,geo,item,year)[0], result);
    }


    protected static Object[] prepareValueForAggregation(Region region, Double value, String flag, String geo, String item, Long year) {
        //Apply flag for value normalization
        if (flag!=null) {
            if (value==null || (value!=0.0 && ("0".equals(flag) || "1".equals(flag)))) // apply flags '0' or '-'
                value = 0.0;
            else if (value!=null && ("4".equals(flag) || "5".equals(flag) || "6".equals(flag) || "7".equals(flag))) // apply flags '.' or '..' or '...' or ':'
                value = null;
        }
        //Apply UMOA key products rules to value
        if (value==null)
            value = isKeyProduct(region, region.getGaulCode(), item, year) || isKeyProduct(region, geo, item, year) ? null : Double.MIN_VALUE;
        //Return prepared data
        return new Object[]{value,geo,item,year};
    }


}
