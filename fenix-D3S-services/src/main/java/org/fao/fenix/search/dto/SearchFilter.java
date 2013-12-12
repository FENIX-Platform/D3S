package org.fao.fenix.search.dto;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.fao.fenix.search.dto.valueFilters.ColumnValueFilter;
import org.fao.fenix.server.utils.JSONUtils;

public class SearchFilter {
	

	private LinkedHashMap<String, Collection<ColumnValueFilter>> fields = new LinkedHashMap<String, Collection<ColumnValueFilter>>();
	private LinkedHashMap<String, Collection<ColumnValueFilter>> dimensions = new LinkedHashMap<String, Collection<ColumnValueFilter>>();
	private Map<String, Object> parameters = new HashMap<String, Object>();

	//GET-SET
	public LinkedHashMap<String, Collection<ColumnValueFilter>> getFields() {
		return fields;
	}
	public void setFields(LinkedHashMap<String, Collection<ColumnValueFilter>> fields) {
		this.fields = fields;
	}
	public LinkedHashMap<String, Collection<ColumnValueFilter>> getDimensions() {
		return dimensions;
	}
	public void setDimensions(LinkedHashMap<String, Collection<ColumnValueFilter>> dimensions) {
		this.dimensions = dimensions;
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	//Utils
	public void addFieldFilter(String name, ColumnValueFilter filterValue) {
		Collection<ColumnValueFilter> fieldValues = fields.get(name);
		if (fieldValues==null)
			fields.put(name, fieldValues=new LinkedList<ColumnValueFilter>());
		fieldValues.add(filterValue);
	}
	public void addDimensionFilter(String name, ColumnValueFilter filterValue) {
		Collection<ColumnValueFilter> dimensionValues = dimensions.get(name);
		if (dimensionValues==null)
			dimensions.put(name, dimensionValues=new LinkedList<ColumnValueFilter>());
		dimensionValues.add(filterValue);
	}
    public Object getParameter (String key) {
        return parameters.get(key);
    }
    public Map<String,OutputParameters> getOutParameters() {
        return parameters!=null ? (Map<String,OutputParameters>)parameters.get("output") : null;
    }
    public OutputParameters getOutParameter(String key) {
        Map<String,OutputParameters> outParams = parameters!=null ? (Map<String,OutputParameters>)parameters.get("output") : null;
        return outParams!=null ? outParams.get(key) : null;
    }
    public void putOutParameter(String key, OutputParameters value) {
        Map<String,OutputParameters> outParams = parameters!=null ? (Map<String,OutputParameters>)parameters.get("output") : null;
        if (outParams==null) {
            if (parameters==null)
                parameters = new HashMap<String, Object>();
            parameters.put("output", outParams = new HashMap<String, OutputParameters>());
        }
        outParams.put(key,value);
    }



	@Override
	public Object clone() throws CloneNotSupportedException {
		try { return JSONUtils.cloneByJSON(this);
		} catch (Exception e) {
            throw new CloneNotSupportedException();
        }
	}



    //Find unique filter key
    private static final String[] keyBusinessParameters = new String[] {
            "globalAggregation",
            "firstAggregation",
            "secondAggregation"
    };

    @JsonIgnore
    public String getKey() {
        StringBuilder key = new StringBuilder();
        getKey(key,fields);
        getKey(key,dimensions);
        getOutParamsKey(key,getOutParameters());
        getBusinessParamsKey(key,parameters);
        return key.toString();
    }
    @JsonIgnore
    private void getKey(StringBuilder key, Map<String, Collection<ColumnValueFilter>> filter) {
        if (filter!=null)
            for (Map.Entry<String, Collection<ColumnValueFilter>> filterElement : filter.entrySet()) {
                key.append(filterElement.getKey());
                Collection<ColumnValueFilter> filterValue = filterElement.getValue();
                if (filterValue!=null)
                    for (ColumnValueFilter filterValueInstance : filterValue)
                        key.append(filterValueInstance.getKey());
            }
    }

    private void getOutParamsKey(StringBuilder key, Map<String,OutputParameters> outParams) {
        if (outParams!=null)
            for (Map.Entry<String, OutputParameters> filterElement : outParams.entrySet()) {
                key.append(filterElement.getKey());
                OutputParameters filterValue = filterElement.getValue();
                if (filterValue!=null)
                    key.append(filterValue.getIdKey());
            }
    }

    private void getBusinessParamsKey(StringBuilder key, Map<String,Object> businessParams) {
        if (businessParams!=null) {
            for (String keyParam : keyBusinessParameters) {
                Object paramValue = businessParams.get(keyParam);
                if (paramValue!=null)
                    key.append(keyParam).append(paramValue.hashCode());
            }

        }

    }

}
