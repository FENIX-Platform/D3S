package org.fao.fenix.d3s.search.bl.codec.bestMatch;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.bl.codec.Codec;

import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.enterprise.context.Dependent;

@Dependent
public class BestMatchCodec extends Codec {

	@Override
	@SuppressWarnings("unchecked")
	public boolean encodeFilter(ResourceFilter filter, ODocument dataset) throws Exception {
        //return filter converted or not
        return false;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public void decodeData(ResourceFilter filter, ODocument dataset, SearchStep data) throws Exception  {
        cloneResult(data);
    }

}
