package org.fao.fenix.d3s.search.bl.datasetFilter;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.commons.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.commons.search.dto.filter.ColumnValueFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class DatasetDimensionsFilter extends DatasetFilter {
	private static final int MAX_VALUES_TO_CHECK = 1000;

    @Autowired private CodeListLoad clLoadDao;

    @Override
	public Collection<ODocument> filter(ResourceFilter baseFilter, Collection<ODocument> source) throws Exception {
		Map<ODocument,ResourceFilter> encodedFilters = getFlow().getEncodedFilters();
		Collection<ODocument> result = new LinkedList<ODocument>();
		
		for (ODocument dataset : source) {
            ResourceFilter filter = encodedFilters.containsKey(dataset) ? encodedFilters.get(dataset) : baseFilter;
			Map<String,ODocument> columnsByDimension = getFlow().getColumnsByDimension(dataset);
			
			//Verfy dimensions name existence
			if (!columnsByDimension.keySet().containsAll(filter.getData().keySet()))
				continue;
			//Values and datatype prefilter over the differen filter dimensions
			if (!valuesPreFilterCheck(columnsByDimension, filter.getData()))
				continue;
			//If every check is passed
			result.add(dataset);
		}
		return result;
	}
	
	
	
	//Utils
	@SuppressWarnings("unchecked")
	private boolean valuesPreFilterCheck (Map<String,ODocument> columnsByDimension, Map<String,Collection<ColumnValueFilter>> dimensionsFilter) throws Exception {
		for (Map.Entry<String, Collection<ColumnValueFilter>> columnFilterEntry : dimensionsFilter.entrySet()) {
			boolean checkPassed = true;
			Collection<ColumnValueFilter> filterElements = columnFilterEntry.getValue();
			Collection<Object> columnValues = (Collection<Object>)columnsByDimension.get(columnFilterEntry.getKey()).field("values");
			DSDDataType columnDataType = DSDDataType.getByCode((String)columnsByDimension.get(columnFilterEntry.getKey()).field("datatype"));
			//TODO to remove
//			if (columnDataType==DSDDataType.code && columnValues!=null && !columnValues.isEmpty() && columnValues.iterator().next() instanceof String) {
//				ODocument codeSystemO = columnsByDimension.get(columnFilterEntry.getKey()).field("codeSystem");
//				String system = codeSystemO!=null ? (String)codeSystemO.field("system") : null;
//				String version = codeSystemO!=null ? (String)codeSystemO.field("version") : null;
//				OGraphDatabase database = getDatabase(OrientDatabase.msd);
//				if (system!=null && version!=null) {
//					Collection<Object> columnValuesO = new LinkedList<Object>();
//					for (Object o : columnValues)					
//						columnValuesO.add(clLoadDao.loadCodeO(system, version, (String)o, database));
//					columnValues = columnValuesO;
//				}
//			}
			//TODO end to remove
			
			if (filterElements!=null && filterElements.size()>0 && columnDataType!=null && columnValues!=null && columnValues.size()>0 && columnValues.size()<=MAX_VALUES_TO_CHECK) {
				checkPassed = false;
				for (ColumnValueFilter filterElement : filterElements) { //OR condition about different value filters over the same column
					//Column datatype versus Filter type compatibility check
					if (!filterElement.getType().hasScope(columnDataType))
						continue;
					//Column values check
					if (!checkValues(filterElement, columnDataType, columnValues))
						continue;
					//All checks passed
					checkPassed = true;
					break;
				}
			}
			if (!checkPassed) //AND condition over different columns check
				return false;
		}
		return true;
	}

    private boolean checkValues(ColumnValueFilter filterElement, DSDDataType columnDataType, Collection<Object> values) throws Exception {
		if (values!=null)
			for (Object value : values)
				if (checkValue(filterElement, columnDataType, value)) //OR condition over different values of the same column
					return true;
		return false;
	}

    @SuppressWarnings("unchecked")
	private boolean checkValue(ColumnValueFilter filterElement, DSDDataType columnDataType, Object value) throws Exception {
        OGraphDatabase database = getFlow().getMsdDatabase();
        CharSequence v;
        switch (filterElement.getType()) {
            case numberInterval:
                return filterElement.getFrom().compareTo(((Number)value).longValue())<=0 && filterElement.getTo().compareTo(((Number)value).longValue())>=0;
            case dateInterval:
                Date date = null;
                switch(columnDataType) {
                    case date:
                        date = (Date)value;
                        break;
                    case year:
                        Calendar c = Calendar.getInstance();
                        c.set(((Number)value).intValue(),Calendar.JANUARY,1);
                        date = c.getTime();
                        break;
                }
                return date!=null && filterElement.getFromDate().compareTo(date)<=0 && filterElement.getToDate().compareTo(date)>=0;
            case text:
                return filterElement.getText().equals(value);
            case like:
                return filterElement.getRegExpPatternLike().matcher((CharSequence)value).matches();
            case iText:
                try { v = ((Map<String,String>)value).get(filterElement.getLanguage()); } catch (Exception ex) { v = null; }
                return v!=null && filterElement.getText().equals(v);
            case iLike:
                try { v = ((Map<String,String>)value).get(filterElement.getLanguage()); } catch (Exception ex) { v = null; }
                return v!=null && filterElement.getRegExpPatternLike().matcher(v).matches();
            case code:
                ODocument filterCodeO = clLoadDao.loadCodeO(filterElement.getCode().getSystemKey(), filterElement.getCode().getSystemVersion(), filterElement.getCode().getCode(), database);
                ODocument codeO = database.load((ORID)value);
                return filterCodeO != null && codeO != null && clLoadDao.hasChildO(filterCodeO, codeO, database);
            case document:
                return toRID(filterElement.getId()).equals(value);
        }
		return false;
	}
	
	

}
