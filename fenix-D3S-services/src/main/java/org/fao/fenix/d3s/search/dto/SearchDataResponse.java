package org.fao.fenix.d3s.search.dto;

import java.util.Collection;
import java.util.LinkedList;

import org.fao.fenix.commons.msd.dto.full.dm.DM;

public class SearchDataResponse extends SearchResponse {
	
	private DM dm;
	private Collection<Object[]> data = new LinkedList<Object[]>();

	
	//GET-SET
	public DM getDm() {
		return dm;
	}
	public void setDm(DM structure) {
		this.dm = structure;
	}
	public Collection<Object[]> getData() {
		return data;
	}
	public void setData(Collection<Object[]> data) {
		this.data = data;
		if (data!=null)
			setCount(data.size());
	}
	
	
	//Utils
	public void addRow(Object[] row) {
		if (data!=null && data instanceof Collection)
			((Collection<Object[]>)data).add(row);
	}
	
	@Override
	public SearchDataResponse clone() throws CloneNotSupportedException {
		SearchDataResponse clone = (SearchDataResponse)super.clone();
		
		clone.data = data;
		clone.dm = dm;

		return clone;
	}
	
}
