package org.fao.fenix.search.services.rest;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dsd.DSD;
import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.search.dto.*;
import org.fao.fenix.search.services.impl.BasicDataSearch;
import org.fao.fenix.search.services.impl.BasicMetadataSearch;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.fao.fenix.server.utils.JSONUtils;

@Path("find")
public class Search implements org.fao.fenix.search.services.spi.Search {
    @Context HttpServletRequest request;

    @Override
	public SearchDataResponse getDataBasicAlgorithm(SearchFilter filter) throws Exception {
        decodeOutputParameters(filter);
        return (SearchDataResponse)SpringContext.getBean(BasicDataSearch.class).search(filter);
	}
	
    @Override
	public SearchDataResponse getDataBasicAlgorithmTest(SearchFilter filter) throws Exception {
        decodeOutputParameters(filter);
        Boolean geoOut = ((OutputParameters)((Map<String,Object>)filter.getParameters().get("output")).get("GEO")).getOut();
        return (SearchDataResponse)getFakeDataToMax(geoOut);
	}

	@Override
	public SearchMetadataResponse getMetadataBasicAlgorithm(SearchFilter filter) throws Exception {
        decodeOutputParameters(filter);
        return (SearchMetadataResponse)SpringContext.getBean(BasicMetadataSearch.class).search(filter);
	}



    //Utils
    //Set output parameters as a Map<String,OutputParameters>
    @SuppressWarnings("unchecked")
    private void decodeOutputParameters(SearchFilter filter) throws Exception {
        Map<String,Object> parameters = filter.getParameters();
        Map<String,Object> outputParameters = parameters!=null ? (Map<String,Object>)parameters.get("output") : null;
        if (outputParameters!=null) {
            Map<String,OutputParameters> outputParametersesDecoded = new HashMap<>();
            for (Map.Entry<String,Object> pEntry : outputParameters.entrySet())
                outputParametersesDecoded.put(pEntry.getKey(), JSONUtils.convertValue(pEntry.getValue(), OutputParameters.class));
            parameters.put("output",outputParametersesDecoded);
        }
    }


    //TODO to delete
    private SearchResponse getFakeDataToMax(Boolean geoOut) {
        //Metadata
        DSD dsd = new DSD();
        dsd.setStartDate(new Date());
        dsd.setEndDate(new Date());
        DSDContextSystem context = new DSDContextSystem();
        context.setName("D3S");
        dsd.setContextSystem(context);
        DM metadata = new DM();
        metadata.addTitle("EN","Dataset aggregato per Max");
        metadata.addTitle("FR","Dataset aggregato per Max");
        metadata.setDsd(dsd);

        //Col 0
        DSDColumn column = new DSDColumn();
        DSDDimension dimension = new DSDDimension();
        if (geoOut) {
            dimension.setName("GEO");
            dimension.addTitle("EN", "Country");
            dimension.addTitle("FR", "Country");
            column.setDimension(dimension);
            column.setCodesLevel(1);
            column.setCodeSystem(new CodeSystem("GAUL", "1.0"));//TODO
            column.setColumnId(dimension.getName());
            column.setTitle(dimension.getTitle());
            column.setDataType(DSDDataType.code);
            column.setValues(Arrays.<Object>asList("29","66","155","42"));
            dsd.addColumn(column);
        }
        //Col 1
        column = new DSDColumn();
        dimension = new DSDDimension();
        dimension.setName("ITEM");
        dimension.addTitle("EN", "Item");
        dimension.addTitle("FR", "Item");
        column.setDimension(dimension);
        column.setCodesLevel(1);
        column.setCodeSystem(new CodeSystem("Crops", "1.0"));//TODO
        column.setColumnId(dimension.getName());
        column.setTitle(dimension.getTitle());
        column.setDataType(DSDDataType.code);
        column.setValues(Arrays.<Object>asList("486","157","489","748","531")); //Production
        dsd.addColumn(column);
        //Col 2
        column = new DSDColumn();
        dimension = new DSDDimension();
        dimension.setName("TIME");
        dimension.addTitle("EN", "Reference time");
        dimension.addTitle("FR", "Reference time");
        column.setDimension(dimension);
        column.setColumnId(dimension.getName());
        column.setTitle(dimension.getTitle());
        column.setDataType(DSDDataType.year);
        dsd.addColumn(column);
        //Col 3
        column = new DSDColumn();
        dimension = new DSDDimension();
        dimension.setName("VALUE");
        dimension.addTitle("EN", "Value");
        dimension.addTitle("FR", "Value");
        column.setDimension(dimension);
        column.setColumnId(dimension.getName());
        column.setTitle(dimension.getTitle());
        column.setDataType(DSDDataType.number);
        dsd.addColumn(column);
        //Col 4
        column = new DSDColumn();
        dimension = new DSDDimension();
        dimension.setName("ELEMENT");
        dimension.addTitle("EN", "Element");
        dimension.addTitle("FR", "Element");
        column.setDimension(dimension);
        column.setCodesLevel(1);
        column.setCodeSystem(new CodeSystem("Elements", "1.0"));
        column.setColumnId(dimension.getName());
        column.setTitle(dimension.getTitle());
        column.setDataType(DSDDataType.code);
        column.setVirtualColumn("INTERNAL");
        column.setValues(Arrays.<Object>asList("1")); //Production
        dsd.addColumn(column);
        //Col 5
        column = new DSDColumn();
        dimension = new DSDDimension();
        dimension.setName("ITEM_TYPE");
        dimension.addTitle("EN", "Item type");
        dimension.addTitle("FR", "Item type");
        column.setDimension(dimension);
        column.setColumnId(dimension.getName());
        column.setTitle(dimension.getTitle());
        column.setDataType(DSDDataType.text);
        column.setVirtualColumn("INTERNAL");
        column.setValues(Arrays.<Object>asList("Commodity"));
        dsd.addColumn(column);

        //Response
        SearchDataResponse response = new SearchDataResponse();
        response.setCount(10);
        response.setDm(metadata);

        //Data
        if (geoOut) {
            response.addRow(new Object[] {"29","486",1992,10856}); //Bananas
            response.addRow(new Object[] {"29","157",1992,100}); //Sugar beet
            response.addRow(new Object[] {"29","489",1992,356}); //Plantains
            response.addRow(new Object[] {"29","748",1992,3284}); //Peppermint
            response.addRow(new Object[] {"29","157",1993,25}); //Sugar beet
            response.addRow(new Object[] {"29","748",1993,32589}); //Peppermint
            response.addRow(new Object[] {"29","531",2010,33326}); //Cherries
            response.addRow(new Object[] {"29","157",2010,145}); //Sugar beet
            response.addRow(new Object[] {"29","486",2010,178}); //Bananas
            response.addRow(new Object[] {"29","748",2010,14588}); //Peppermint

            response.addRow(new Object[] {"66","486",1992,10856}); //Bananas
            response.addRow(new Object[] {"66","157",1992,100}); //Sugar beet
            response.addRow(new Object[] {"66","489",1992,356}); //Plantains
            response.addRow(new Object[] {"66","748",1992,3284}); //Peppermint
            response.addRow(new Object[] {"66","157",1993,25}); //Sugar beet
            response.addRow(new Object[] {"66","748",1993,32589}); //Peppermint
            response.addRow(new Object[] {"66","531",2010,33326}); //Cherries
            response.addRow(new Object[] {"66","157",2010,145}); //Sugar beet
            response.addRow(new Object[] {"66","486",2010,178}); //Bananas
            response.addRow(new Object[] {"66","748",2010,14588}); //Peppermint

            response.addRow(new Object[] {"155","486",1992,10856}); //Bananas
            response.addRow(new Object[] {"155","157",1992,100}); //Sugar beet
            response.addRow(new Object[] {"155","489",1992,356}); //Plantains
            response.addRow(new Object[] {"155","748",1992,3284}); //Peppermint
            response.addRow(new Object[] {"155","157",1993,25}); //Sugar beet
            response.addRow(new Object[] {"155","748",1993,32589}); //Peppermint
            response.addRow(new Object[] {"155","531",2010,33326}); //Cherries
            response.addRow(new Object[] {"155","157",2010,145}); //Sugar beet
            response.addRow(new Object[] {"155","486",2010,178}); //Bananas
            response.addRow(new Object[] {"155","748",2010,14588}); //Peppermint

            response.addRow(new Object[] {"42","486",1992,10856}); //Bananas
            response.addRow(new Object[] {"42","157",1992,100}); //Sugar beet
            response.addRow(new Object[] {"42","489",1992,356}); //Plantains
            response.addRow(new Object[] {"42","748",1992,3284}); //Peppermint
            response.addRow(new Object[] {"42","157",1993,25}); //Sugar beet
            response.addRow(new Object[] {"42","748",1993,32589}); //Peppermint
            response.addRow(new Object[] {"42","531",2010,33326}); //Cherries
            response.addRow(new Object[] {"42","157",2010,145}); //Sugar beet
            response.addRow(new Object[] {"42","486",2010,178}); //Bananas
            response.addRow(new Object[] {"42","748",2010,14588}); //Peppermint
        } else {
            response.addRow(new Object[] {"486",1992,10856}); //Bananas
            response.addRow(new Object[] {"157",1992,100}); //Sugar beet
            response.addRow(new Object[] {"489",1992,356}); //Plantains
            response.addRow(new Object[] {"748",1992,3284}); //Peppermint
            response.addRow(new Object[] {"157",1993,25}); //Sugar beet
            response.addRow(new Object[] {"748",1993,32589}); //Peppermint
            response.addRow(new Object[] {"531",2010,33326}); //Cherries
            response.addRow(new Object[] {"157",2010,145}); //Sugar beet
            response.addRow(new Object[] {"486",2010,178}); //Bananas
            response.addRow(new Object[] {"748",2010,14588}); //Peppermint
        }

        //Return
        return response;
    }
}
