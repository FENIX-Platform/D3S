package org.fao.fenix.d3s.search.bl.converter;

import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class BasicConverter extends Converter {
	

	@Override
	public void convertData(SearchFilter filter, SearchStep data) throws Exception {
        cloneResult(data);
		//TODO
	}

}
