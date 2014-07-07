package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.CodeList;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import java.util.Collection;
import java.util.LinkedList;

public class CodeListDao extends OrientDao {

    public Collection<CodeList> getCodeLists() throws Exception {
        Collection<CodeList> codeLists = new LinkedList<>();
        Collection<MeIdentification> codeListResources = select(MeIdentification.class, "select from MeIdentification where meContent.resourceRepresentationType = ?", RepresentationType.codelist.name());
        for (MeIdentification codeListResource : codeListResources) {
            Collection<Code> rootLevel = select(Code.class, "select from Code where codeList = ? and level = 1", codeListResource);
            codeLists.add(new CodeList(codeListResource, rootLevel));
        }
        return codeLists;
    }

    public CodeList getCodeList(String rid) throws Exception {
        MeIdentification codeListResource = loadBean(rid, MeIdentification.class);
        if (codeListResource!=null) {
            Collection<Code> rootLevel = select(Code.class, "select from Code where codeList = ? and level = 1", codeListResource);
            return new CodeList(codeListResource, rootLevel);
        } else {
            return null;
        }
    }


    public CodeList insertCodeList (CodeList codeList) throws Exception {
        MeIdentification codeListResource = newCustomEntity(codeList.getMetadata());
        Collection<Code> rootLevel = codeList.getData()!=null ? saveCustomEntity(normalization(codeListResource, codeList.getData()),false,true) : null;
        return new CodeList ( codeListResource, rootLevel );
    }
    public CodeList updateCodeList (CodeList codeList, boolean overwrite) throws Exception {
        MeIdentification codeListResource = saveCustomEntity(codeList.getMetadata(),overwrite);
        Collection<Code> rootLevel = codeList.getData()!=null ? saveCustomEntity(normalization(codeListResource, codeList.getData()),overwrite,true) : null;
        return new CodeList ( codeListResource, rootLevel );
    }

    private Collection<Code> normalization (MeIdentification codeListResource, Collection<Code> codes) {
        return null; //TODO
    }


}
