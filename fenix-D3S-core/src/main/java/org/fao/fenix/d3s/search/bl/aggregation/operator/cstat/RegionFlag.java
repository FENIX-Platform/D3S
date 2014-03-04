package org.fao.fenix.d3s.search.bl.aggregation.operator.cstat;

import java.sql.SQLException;

public abstract class RegionFlag extends RegionAggregation {

    public Object getResult(Region region, boolean allCountries) throws SQLException {
        if (!"5".equals(result) && allCountries)
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


    protected static Object[] prepareValueForAggregation(Region region, String flag, Double value, String geo, String item, Long year) {
        //Apply flag for value normalization
        if (flag!=null) {
            if (value==null || (value!=0.0 && ("0".equals(flag) || "1".equals(flag)))) // apply flags '0' or '-'
                value = 0.0;
            else if (value!=null && ("4".equals(flag) || "5".equals(flag) || "6".equals(flag) || "7".equals(flag))) // apply flags '.' or '..' or '...' or ':'
                value = null;
        }
        //Apply UMOA key products rules to value
        if (value==null)
            flag = isKeyProduct(region, region.getGaulCode(), item, year) || isKeyProduct(region, geo, item, year) ? "5" : null;
        else //For good values only 'e' flag is valid
            flag = "2".equals(flag) ? flag : null;
        //Return prepared data
        return new Object[]{flag,geo,item,year};
    }
}
