package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.templates.codeList.CodeList;
import org.fao.fenix.d3s.msd.dao.CodeListDao;
import org.fao.fenix.d3s.msd.services.spi.CodeLists;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.Date;

@Path("codeLists")
public class CodeListsService implements CodeLists {
    @Inject private CodeListDao dao;

    @Override
    public Collection<CodeList> getCodeLists(Date from, Date to) throws Exception {
        return CodeList.getInstances(dao.getCodeLists());
    }

    @Override
    public CodeList getCodeList(String rid) throws Exception {
        return CodeList.getInstance(dao.getCodeList(rid));
    }

    @Override
    public CodeList insertCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception {
        return CodeList.getInstance(dao.insertCodeList(codeList));
    }

    @Override
    public CodeList updateCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception {
        return CodeList.getInstance(dao.updateCodeList(codeList, true));
    }

    @Override
    public CodeList appendCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception {
        return CodeList.getInstance(dao.updateCodeList(codeList, false));
    }
}
