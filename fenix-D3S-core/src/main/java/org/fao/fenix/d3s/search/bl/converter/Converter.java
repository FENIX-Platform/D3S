package org.fao.fenix.d3s.search.bl.converter;

import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;

public abstract class Converter extends SearchStep {
	
	public abstract void convertData (SearchFilter filter, SearchStep data) throws Exception;
	

}
