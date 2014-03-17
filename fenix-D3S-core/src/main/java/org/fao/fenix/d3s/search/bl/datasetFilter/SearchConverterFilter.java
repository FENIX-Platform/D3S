package org.fao.fenix.d3s.search.bl.datasetFilter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchFlow;
import org.fao.fenix.d3s.search.bl.codec.bestMatch.BestMatchCodec;
import org.fao.fenix.d3s.search.dto.OutputParameters;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.commons.search.dto.filter.ColumnValueFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class SearchConverterFilter extends DatasetFilter {
	@Autowired private BestMatchCodec filterEncoder;

	@Override
	public Collection<ODocument> filter(ResourceFilter baseFilter, Collection<ODocument> source) throws Exception {
        SearchFlow flowData = getFlow();
        //Complete filter if there are no output parameters
        Map<String,OutputParameters> outputParametersMap = flowData.getBusinessOutputParameters();
        if (source!=null && source.size()>0 && outputParametersMap.size()==0)
            for (String dimension : flowData.getColumnsByDimension(source.iterator().next()).keySet())
                outputParametersMap.put(dimension,new OutputParameters(true, !"VALUE".equals(dimension)));

		//TODO rivedere per eseguire la transcodifica una sola volta da una certa codifica ad un'altra
        ResourceFilter filterCopy = (ResourceFilter)baseFilter.clone();
        boolean filterChanged;
        for (ODocument dataset : source) {
            //Resize filter to dataset needs
            LinkedHashMap<String, Collection<ColumnValueFilter>> dimensionFilter = baseFilter.getData();
            Map<String,ODocument> dimColMap = getFlow().getColumnsByDimension(dataset);
            LinkedHashMap<String, Collection<ColumnValueFilter>> dimensionFilterCopy = new LinkedHashMap<String, Collection<ColumnValueFilter>>();

            for (Map.Entry<String, Collection<ColumnValueFilter>> dimension : dimensionFilter.entrySet()) {
                ODocument column = dimColMap.get(dimension.getKey());
                String virtualColumn = column.field("virtualColumn");
                Collection<Object> values = column.field("values");
                if (!"INTERNAL".equals(virtualColumn) && (values==null || values.size()!=1))
                    dimensionFilterCopy.put(dimension.getKey(), dimension.getValue());
            }

            if (filterChanged = dimensionFilterCopy.size()!=dimensionFilter.size())
                filterCopy.setData(dimensionFilterCopy);

            //Convert codes to dataset codelists
			if (filterEncoder.encodeFilter(filterCopy, dataset))
                filterChanged = true;

            //add changed filter to flow
            if (filterChanged) {
                getFlow().addEncodedFilter(dataset, filterCopy);
                filterCopy = (ResourceFilter)baseFilter.clone();
            }

        }
		return source;
	}


}
