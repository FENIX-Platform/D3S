package org.fao.fenix.d3s.search.dto;

import java.util.Collection;
import java.util.LinkedList;

import org.fao.fenix.commons.msd.dto.full.dm.DM;

public class SearchMetadataResponse extends SearchResponse {
	
	private Collection<DM> datasets = new LinkedList<DM>();
	
	//GET-SET
	public Collection<DM> getDatasets() {
		return datasets;
	}
	public void setDatasets(Collection<DM> datasets) {
		this.datasets = datasets;
		if (datasets!=null)
			setCount(datasets.size());
	}

	
	//Utils
	public void addDM(DM dataset) {
		if (datasets!=null)
			datasets.add(dataset);
	}
	
	@Override
	public SearchMetadataResponse clone() throws CloneNotSupportedException {
		SearchMetadataResponse clone = (SearchMetadataResponse)super.clone();
		
		clone.datasets = datasets;

		return clone;
	}
	
}
