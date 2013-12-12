package org.fao.fenix.search.bl.merge;

import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.dto.SearchDataResponse;
import org.fao.fenix.search.dto.SearchFilter;

import java.util.Collection;

public abstract class Merge extends SearchStep {
	
	public abstract SearchDataResponse merge(SearchFilter filter, Collection<SearchDataResponse> data);

}
