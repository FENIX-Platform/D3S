package org.fao.fenix.d3s.search.bl.codec.bestMatch;

import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.msd.dao.cl.CodeListConverter;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLinkLoad;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.bl.codec.Codec;
import org.fao.fenix.d3s.search.bl.codec.CodecCodeListUtils;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
@Scope("prototype")
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
