package org.fao.fenix.search.bl.converter;

import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.SearchResponse;
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
