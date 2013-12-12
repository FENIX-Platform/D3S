package org.fao.fenix.search.bl.codec;

import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.dto.SearchDataResponse;
import org.fao.fenix.search.dto.SearchFilter;

import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class Codec extends SearchStep {
	
	public abstract boolean encodeFilter(SearchFilter filter, ODocument dataset) throws Exception;
	public abstract void decodeData(SearchFilter filter, ODocument dataset, SearchStep data) throws Exception;

}
