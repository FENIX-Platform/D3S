package org.fao.fenix.d3s.search.bl.codec;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class Codec extends SearchStep {
	
	public abstract boolean encodeFilter(ResourceFilter filter, ODocument dataset) throws Exception;
	public abstract void decodeData(ResourceFilter filter, ODocument dataset, SearchStep data) throws Exception;

}
