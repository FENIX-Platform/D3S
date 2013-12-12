package org.fao.fenix.search.bl.converter;

import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.SearchResponse;

public abstract class Converter extends SearchStep {
	
	public abstract void convertData (SearchFilter filter, SearchStep data) throws Exception;
	

}
