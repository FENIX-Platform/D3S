package org.fao.fenix.d3s.search.bl.converter;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;

import javax.enterprise.context.Dependent;

@Dependent
public class BasicConverter extends Converter {
	

	@Override
	public void convertData(ResourceFilter filter, SearchStep data) throws Exception {
        cloneResult(data);
	}

}
