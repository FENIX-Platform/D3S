package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CodeListResourceDao extends ResourceDao<Code> {
/*
    public Collection<Resource<Code>> getCodeLists() throws Exception {
        Collection<Resource<Code>> codeLists = new LinkedList<>();
        Collection<MeIdentification> codeListResources = select(MeIdentification.class, "select from MeIdentification where meContent.resourceRepresentationType = ?", RepresentationType.codelist.name());
        for (MeIdentification codeListResource : codeListResources) {
            Collection<Code> rootLevel = select(Code.class, "select from Code where codeList = ? and level = 1", codeListResource);
            codeLists.add(new Resource(codeListResource, rootLevel));
        }
        return codeLists;
    }
*/


    @Override
    public Collection<Code> loadData(MeIdentification metadata) throws Exception {
        return metadata!=null ? select(Code.class, "select from Code where codeList = ? and level = 1", metadata) : null;
    }

    @Override
    protected Collection<Code> insertData(MeIdentification metadata, Collection<Code> data) throws Exception {
        return data!=null ? saveCustomEntity(normalization(metadata, data),false,true) : null;
    }

    @Override
    protected Collection<Code> updateData(MeIdentification metadata, Collection<Code> data, boolean overwrite) throws Exception {
        return data!=null ? saveCustomEntity(normalization(metadata, data),overwrite,true) : null;
    }


    //Utils
    private Collection<Code> normalization (MeIdentification codeListResource, Collection<Code> codes) {
        normalization(codeListResource, codes, null, new HashSet<Code>());
        return codes;
    }

    private void normalization(MeIdentification codeListResource, Collection<Code> codes, Code parentCode, Set<Code> visitedNodes) {
        if (codes!=null)
            for (Code code : codes) {
                if (!visitedNodes.contains(code)) {
                    code.setLevel(1);
                    visitedNodes.add(code);
                }

                code.setCodeList(codeListResource);                                     //Set codelist
                if (parentCode!=null) {
                    code.setLevel(Math.max(parentCode.getLevel()+1, code.getLevel()));      //Update level
                    code.addParent(parentCode);                                             //Add parents links
                }

                normalization(codeListResource, code.getChildren(), code, visitedNodes);
            }
    }
}
