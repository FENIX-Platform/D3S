package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/cl")
public class StoreCodeList implements org.fao.fenix.msd.services.spi.StoreCodeList {
    @Context HttpServletRequest request;

	//code list
	@Override
	public void newCodeList(CodeSystem cl) throws Exception {
		SpringContext.getBean(Store.class).newCodeList(cl);
	}
	@Override
	public void updateCodeList(CodeSystem cl) throws Exception {
		if (SpringContext.getBean(Store.class).updateCodeList(cl,true)<=0)
            throw new NoContentException("");
	}
	@Override
	public void appendCodeList(CodeSystem cl) throws Exception {
        if (SpringContext.getBean(Store.class).updateCodeList(cl,true)<=0)
            throw new NoContentException("");
	}
    @Override
    public void restoreCodeList() throws Exception {
        if (SpringContext.getBean(Store.class).codeListIndex(null, null)<=0)
            throw new NoContentException("");
    }
    @Override
    public void restoreCodeList(String system, String version) throws Exception {
        if (SpringContext.getBean(Store.class).codeListIndex(system, version)<=0)
            throw new NoContentException("");
    }

    //code
	@Override
	public void updateCode(Code code) throws Exception {
        if (SpringContext.getBean(Store.class).updateCode(code)<=0)
            throw new NoContentException("");
	}
	

	//keyword
	@Override
	public void newKeyword(String keyword) throws Exception {
		SpringContext.getBean(Store.class).newKeyword(keyword);
	}

	//relationship
	@Override
	public void newRelationship(CodeRelationship relation) throws Exception {
		SpringContext.getBean(Store.class).newRelationship(relation);
	}
	@Override
	public void newRelationship(Collection<CodeRelationship> relation) throws Exception {
		SpringContext.getBean(Store.class).newRelationship(relation);
	}
	//conversion
	@Override
	public void newConversion(CodeConversion conversion) throws Exception {
		SpringContext.getBean(Store.class).newConversion(conversion);
	}
	@Override
	public void newConversion(Collection<CodeConversion> conversion) throws Exception {
		SpringContext.getBean(Store.class).newConversion(conversion);
	}
	@Override
	public void updateConversion(CodeConversion conversion) throws Exception {
        if (SpringContext.getBean(Store.class).updateConversion(conversion)<=0)
            throw new NoContentException("");
	}
	//propaedeutic
	@Override
	public void newPropaedeutic(CodePropaedeutic propaedeutic) throws Exception {
		SpringContext.getBean(Store.class).newPropaedeutic(propaedeutic);
	}
	@Override
	public void newPropaedeutic(Collection<CodePropaedeutic> propaedeutic) throws Exception {
		SpringContext.getBean(Store.class).newPropaedeutic(propaedeutic);
	}

}
