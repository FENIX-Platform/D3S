package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.templates.codeList.CodeList;
import org.fao.fenix.commons.msd.dto.templates.codeList.MeIdentification;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.services.spi.CodeLists;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Collection;
import java.util.Date;

@Path("msd/codeLists")
public class CodeListsService implements CodeLists {
    @Inject private CodeListResourceDao dao;


    @Override
    public CodeList getCodeList(String rid) throws Exception {
        return CodeList.getInstance(dao.loadResource(rid));
    }

    @Override
    public CodeList insertCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception {
        return CodeList.getInstance(dao.insertResource(codeList));
    }

    @Override
    public CodeList updateCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception {
        return CodeList.getInstance(dao.updateResource(codeList, true));
    }

    @Override
    public CodeList appendCodeList(Resource<org.fao.fenix.commons.msd.dto.full.Code> codeList) throws Exception {
        return CodeList.getInstance(dao.updateResource(codeList, false));
    }





    @Override
    public Collection<MeIdentification> getCodeListsResource(Date from, Date to) throws Exception {
        return null;
    }

    @Override
    public MeIdentification getCodeListResource(String rid) throws Exception {
        return null;
    }

    @Override
    public MeIdentification insertCodeListResource(org.fao.fenix.commons.msd.dto.full.MeIdentification codeList) throws Exception {
        return null;
    }

    @Override
    public MeIdentification updateCodeListResource(org.fao.fenix.commons.msd.dto.full.MeIdentification codeList) throws Exception {
        return null;
    }

    @Override
    public MeIdentification appendCodeListResource(org.fao.fenix.commons.msd.dto.full.MeIdentification codeList) throws Exception {
        return null;
    }

    @Override
    public MeIdentification getCodeListData(String rid) throws Exception {
        return null;
    }

    @Override
    public MeIdentification insertCodeListData(String rid, Collection<Code> codeList) throws Exception {
        return null;
    }

    @Override
    public MeIdentification updateCodeListData(String rid, Collection<Code> codeList) throws Exception {
        return null;
    }

    @Override
    public MeIdentification appendCodeListData(String rid, Collection<Code> codeList) throws Exception {
        return null;
    }
}
