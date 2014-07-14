package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class CodeListDao extends OrientDao {

    public Collection<Resource<Code>> getCodeLists() throws Exception {
        Collection<Resource<Code>> codeLists = new LinkedList<>();
        Collection<MeIdentification> codeListResources = select(MeIdentification.class, "select from MeIdentification where meContent.resourceRepresentationType = ?", RepresentationType.codelist.name());
        for (MeIdentification codeListResource : codeListResources) {
            Collection<Code> rootLevel = select(Code.class, "select from Code where codeList = ? and level = 1", codeListResource);
            codeLists.add(new Resource(codeListResource, rootLevel));
        }
        return codeLists;
    }

    public Resource<Code> getCodeList(String rid) throws Exception {
        MeIdentification codeListResource = loadBean(rid, MeIdentification.class);
        if (codeListResource!=null) {
            Collection<Code> rootLevel = select(Code.class, "select from Code where codeList = ? and level = 1", codeListResource);
            return new Resource(codeListResource, rootLevel);
        } else {
            return null;
        }
    }


    public Resource<Code> insertCodeList (Resource<Code> codeList) throws Exception {
        MeIdentification codeListResource = newCustomEntity(codeList.getMetadata());
        Collection<Code> rootLevel = codeList.getData()!=null ? saveCustomEntity(normalization(codeListResource, codeList.getData()),false,true) : null;
        return new Resource ( codeListResource, rootLevel );
    }
    public Resource<Code> updateCodeList (Resource<Code> codeList, boolean overwrite) throws Exception {
        MeIdentification codeListResource = saveCustomEntity(codeList.getMetadata(),overwrite);
        Collection<Code> rootLevel = codeList.getData()!=null ? saveCustomEntity(normalization(codeListResource, codeList.getData()),overwrite,true) : null;
        return new Resource ( codeListResource, rootLevel );
    }

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
