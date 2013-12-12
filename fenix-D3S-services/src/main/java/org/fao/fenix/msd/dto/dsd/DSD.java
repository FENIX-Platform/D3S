package org.fao.fenix.msd.dto.dsd;

import org.fao.fenix.msd.dto.common.ValueOperator;
import org.fao.fenix.server.utils.DataUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DSD {

	private Date startDate;
	private Date endDate;
	private Map<String,String> supplemental;
	
	private DSDContextSystem contextSystem;
	private DSDDatasource datasource;
	private Collection<DSDColumn> columns = new LinkedList<DSDColumn>();

    private Collection<ValueOperator> aggregationRules;

	
	//GET-SET
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = DataUtils.roundDate(startDate);
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = DataUtils.roundDate(endDate);
	}
	public Collection<DSDColumn> getColumns() {
		return columns;
	}
	public void setColumns(Collection<DSDColumn> columns) {
		this.columns = columns;
	}
	public DSDContextSystem getContextSystem() {
		return contextSystem;
	}
	public void setContextSystem(DSDContextSystem contextSystems) {
		this.contextSystem = contextSystems;
	}
	public DSDDatasource getDatasource() {
		return datasource;
	}
	public void setDatasource(DSDDatasource datasource) {
		this.datasource = datasource;
	}
	public Map<String, String> getSupplemental() {
		return supplemental;
	}
	public void setSupplemental(Map<String, String> supplemental) {
		this.supplemental = supplemental;
	}
    public Collection<ValueOperator> getAggregationRules() {
        return aggregationRules;
    }
    public void setAggregationRules(Collection<ValueOperator> aggregationRules) {
        this.aggregationRules = aggregationRules;
    }

    //Utils
	public void addColumn(DSDColumn column) {
		this.columns.add(column);
	}
	public void addSupplemental(String language, String supplemental) {
        if (this.supplemental == null)
            this.supplemental = new HashMap<String, String>();
		this.supplemental.put(language, supplemental);
	}
    public void addAggregationRule (ValueOperator rule) {
        if (aggregationRules == null)
            aggregationRules = new LinkedList<ValueOperator>();
        aggregationRules.add(rule);
    }
}