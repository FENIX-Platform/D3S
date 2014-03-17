package org.fao.fenix.d3s.search.bl.datasetFilter;

import java.util.Collection;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class DatasetFilter extends SearchStep {
	
	public abstract Collection<ODocument> filter(ResourceFilter filter, Collection<ODocument> source) throws Exception;
	
}
