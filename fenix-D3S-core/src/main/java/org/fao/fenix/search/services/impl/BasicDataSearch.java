package org.fao.fenix.search.services.impl;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.cache.Cache;
import org.fao.fenix.cache.impl.OrientCache;
import org.fao.fenix.cache.impl.RamCache;
import org.fao.fenix.msd.dao.dm.DMConverter;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.type.DMCopyrightType;
import org.fao.fenix.msd.dto.dm.type.DMDataKind;
import org.fao.fenix.msd.dto.dm.type.DMDataType;
import org.fao.fenix.msd.dto.dsd.DSD;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.bl.aggregation.H2Aggregator;
import org.fao.fenix.search.bl.codec.bestMatch.BestMatchCodec;
import org.fao.fenix.search.bl.converter.BasicConverter;
import org.fao.fenix.search.bl.dataFilter.MultipleStepIterable;
import org.fao.fenix.search.bl.datasetFilter.SearchConverterFilter;
import org.fao.fenix.search.dto.SearchDataResponse;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.SearchResponse;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.fao.fenix.wds.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BasicDataSearch extends SearchOperation {

	//Search main flow
	@Override
	public SearchResponse searchFlow(SearchFilter filter) throws Exception {
        //Init dependencies
        BestMatchCodec codec = SpringContext.getBean(BestMatchCodec.class);
        BasicConverter converter = SpringContext.getBean(BasicConverter.class);
        BasicMetadataSearch metadataSearch = SpringContext.getBean(BasicMetadataSearch.class);
        SearchConverterFilter filterConverter = SpringContext.getBean(SearchConverterFilter.class);

        Cache cacheL1 = SpringContext.getBean(OrientCache.class);
        try { cacheL1.init("search.basic.cache.L1."); } catch (Exception e) { }
        Cache cacheL2 = SpringContext.getBean(RamCache.class);
        try { cacheL2.init("search.basic.cache.L2."); } catch (Exception e) { }

        OGraphDatabase database = getFlow().getMsdDatabase();
        //Dataset filtering
        Collection<ODocument> datasets = metadataSearch.search(filter,database);
        ODocument masterDataset = datasets!=null && datasets.size()>0 ? datasets.iterator().next() : null;
        if (masterDataset==null)
            return null;
        getFlow().setInvolvedDatasets(datasets);

		//Select data from l2 cache
        cacheL2.loadData(filter, datasets);
		if (!cacheL2.hasData()) {
            //Prepare datasets specific filters
            filterConverter.filter(filter,datasets);
            //Select single datasets data
            Collection<SearchStep> results = new LinkedList<SearchStep>();
            Map<ODocument, SearchFilter> encodedFilters = getFlow().getEncodedFilters();
            for (ODocument dataset : datasets)
                try {
                    //Retrieve specific filter
                    SearchFilter daoFilter = encodedFilters.containsKey(dataset) ? encodedFilters.get(dataset) : filter;
                    //Select row data
                    cacheL1.loadData(daoFilter, dataset);
                    if (!cacheL1.hasData()) {
                        //Select dataset dao
                        Dao dao = getDao((ODocument)dataset.field("dsd.datasource"));
                        dao.initStructure(daoFilter, dataset, true, false);
                        dao.load(daoFilter, dataset);
                        //Save into level 1 cache
                        cacheL1.storeData(daoFilter,dataset,dao);
                    }
                    //Data normalization
                    codec.decodeData(daoFilter, dataset, cacheL1);
                    converter.convertData(daoFilter, codec);
                    //first level aggregation
                    H2Aggregator aggregatorL1 = SpringContext.getBean(H2Aggregator.class);
                    aggregatorL1.init(converter, daoFilter, dataset);
                    //Add result partial result
                    results.add(aggregatorL1);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    throw new NoSuchFieldException("Dataset '"+dataset.field("uid")+"' discarded because of missing column.");
                }
            if (results.size()==1) {
                //Store result into l2 cache
                cacheL2.storeData(filter, datasets, results.iterator().next());
            } else if (results.size()>1) {
                //Prepare data convergence
                MultipleStepIterable bridge = SpringContext.getBean(MultipleStepIterable.class);
                bridge.initStructure(filter,masterDataset,false,false);
                bridge.addAll(results);
                //Second level aggregation
                H2Aggregator aggregatorL2 = SpringContext.getBean(H2Aggregator.class);
                aggregatorL2.init(bridge,filter,null);
                //Store result into l2 cache
                cacheL2.storeData(filter, datasets, aggregatorL2);
            }
		}
        //Prepare data for output
        MultipleStepIterable outBridge = SpringContext.getBean(MultipleStepIterable.class);
        outBridge.initStructure(filter,masterDataset,false,true);
        outBridge.add(cacheL2);

        //Build and return response
        SearchResponse response = outBridge.hasData() ? buildResponse(filter,outBridge,datasets.size()==1 ? masterDataset : null) : null;
        return response;
	}


    //Utils
    @SuppressWarnings("unchecked")
	private Dao getDao(ODocument datasource) throws Exception {
        Dao dao = (Dao)SpringContext.getBean((String) datasource.field("dao"));
        dao.init((Map<String,String>)datasource.field("reference"));
        return dao;
    }

}
