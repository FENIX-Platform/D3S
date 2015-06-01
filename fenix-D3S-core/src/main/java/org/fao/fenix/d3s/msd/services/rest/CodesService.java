package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.find.dto.filter.CodesFilter;
import org.fao.fenix.commons.msd.dto.full.MeContent;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.services.spi.Codes;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.*;

@Path("msd/codes")
public class CodesService implements Codes {
    @Inject private CodeListResourceDao dao;




    @Override
    public Collection<Code> getCodes(CodesFilter filter) throws Exception {
        MeIdentification metadata = filter.rid!=null ? dao.loadMetadata(filter.rid,null) : dao.loadMetadata(filter.uid,filter.version);
        return filter.label!=null && !filter.label.trim().equals("") ? loadCodes(metadata, filter.label) : loadCodes(metadata, filter.level, filter.levels, filter.codes);
    }


    //Logic
    private Collection<Code> loadCodes(MeIdentification metadata, Integer level, Integer levels, Collection<String> codes) throws Exception {
        MeContent meContent = metadata!=null ? metadata.getMeContent() : null;
        if (meContent!=null && meContent.getResourceRepresentationType() == RepresentationType.codelist) {
            //Filter normalization
            String[] codesArray = codes!=null && codes.size()>0 ? codes.toArray(new String[codes.size()]) : null;
            level = level==null && codesArray==null ? new Integer(1) : level;
            //Retrieve data
            Collection<org.fao.fenix.commons.msd.dto.full.Code> data = dao.loadData(metadata, null, level, codesArray);
            //Return data proxy
            if (data!=null && data.size()>0) {
                if (level != null)
                    Code.levelInfo.set(new Integer[]{level, levels != null ? level + levels - 1 : null});
                else if (levels!=null)
                    data = getInjectLevelDataWrapper(data, levels);
                return ResponseBeanFactory.getInstances(data, Code.class);
            }
        }
        return null;
    }
    private Collection<Code> loadCodes(MeIdentification metadata, String label) throws Exception {
        MeContent meContent = metadata!=null ? metadata.getMeContent() : null;
        if (meContent!=null && meContent.getResourceRepresentationType() == RepresentationType.codelist) {
            return loadCodes(
                    metadata,
                    null,
                    1,
                    fillCodesPath(dao.loadData(metadata, label, null), new HashSet<String>())
            );
        }
        return null;
    }
    private Set<String> fillCodesPath (Collection<org.fao.fenix.commons.msd.dto.full.Code> codes, Set<String> codesName) {
        if (codes!=null && codes.size()>0)
            for (org.fao.fenix.commons.msd.dto.full.Code code : codes) {
                codesName.add(code.getCode());
                fillCodesPath(code.getParents(), codesName);
            }
        return codesName;
    }


    //Utils
    private Collection<org.fao.fenix.commons.msd.dto.full.Code> getInjectLevelDataWrapper (final Collection<org.fao.fenix.commons.msd.dto.full.Code> data, final Integer levels) {
        return new LinkedList<org.fao.fenix.commons.msd.dto.full.Code>() {
            @Override
            public int size() {
                return data.size();
            }

            @Override
            public boolean isEmpty() {
                return data.isEmpty();
            }

            @Override
            public Iterator<org.fao.fenix.commons.msd.dto.full.Code> iterator() {
                final Iterator<org.fao.fenix.commons.msd.dto.full.Code> dataIterator = data.iterator();
                return new Iterator<org.fao.fenix.commons.msd.dto.full.Code>() {
                    @Override
                    public boolean hasNext() {
                        return dataIterator.hasNext();
                    }

                    @Override
                    public org.fao.fenix.commons.msd.dto.full.Code next() {
                        org.fao.fenix.commons.msd.dto.full.Code code = dataIterator.next();
                        if (code!=null)
                            Code.levelInfo.set(new Integer[]{code.getLevel(), levels != null ? code.getLevel() + levels - 1 : null});
                        return code;
                    }

                    @Override
                    public void remove() {
                        dataIterator.remove();
                    }
                };
            }
        };
    }
}
