package org.fao.fenix.d3s.search.bl.converter;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;

public abstract class Converter extends SearchStep {
	
	public abstract void convertData (ResourceFilter filter, SearchStep data) throws Exception;
	

}
