package org.fao.fenix.d3s.search.bl.merge;

import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchDataResponse;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import java.util.Collection;

public abstract class Merge extends SearchStep {
	
	public abstract SearchDataResponse merge(SearchFilter filter, Collection<SearchDataResponse> data);

}
