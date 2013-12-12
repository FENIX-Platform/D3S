package org.fao.fenix.search.bl.datasetFilter;

import java.util.Collection;

import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.dto.SearchFilter;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class DatasetFilter extends SearchStep {
	
	public abstract Collection<ODocument> filter(SearchFilter filter, Collection<ODocument> source) throws Exception;
	
}
