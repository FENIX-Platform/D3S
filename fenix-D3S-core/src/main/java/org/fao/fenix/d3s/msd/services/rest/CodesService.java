package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.data.CodesFilter;
import org.fao.fenix.commons.msd.dto.full.MeContent;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.services.spi.Codes;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Collection;

@Path("msd/codes")
public class CodesService implements Codes {
    @Inject private CodeListResourceDao dao;




    @Override
    public Collection<Code> getCodes(CodesFilter filter) throws Exception {
        MeIdentification metadata = filter.getRid()!=null ? dao.loadMetadata(filter.getRid(),null) : dao.loadMetadata(filter.getUid(),filter.getVersion());
        return loadCodes(metadata, filter.getLevel(), filter.getLevels(), filter.getCodes());
    }


    //Logic
    private Collection<Code> loadCodes(MeIdentification metadata, Integer level, Integer levels, Collection<String> codes) throws Exception {
        MeContent meContent = metadata!=null ? metadata.getMeContent() : null;
        if (meContent!=null && meContent.getResourceRepresentationType() == RepresentationType.codelist) {
            Code.levelInfo.set(new Integer[] { level, level+levels-1 } );
            return ResponseBeanFactory.getInstances(dao.loadData(metadata, level, codes!=null?codes.toArray(new String[codes.size()]):null), Code.class);
        }
        return null;
    }
}
