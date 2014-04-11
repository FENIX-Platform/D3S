package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.cl.CodeRelationship;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.msd.services.impl.Store;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

@Path("msd/cl")
public class StoreCodeList implements org.fao.fenix.d3s.msd.services.spi.StoreCodeList {
    @Context HttpServletRequest request;

	//code list
	@Override
	public String newCodeList(CodeSystem cl) throws Exception {
		return SpringContext.getBean(Store.class).newCodeList(cl);
	}
	@Override
	public Integer updateCodeList(CodeSystem cl, boolean all) throws Exception {
        int count = SpringContext.getBean(Store.class).updateCodeList(cl, false, all);
		if (count<=0)
            throw new NoContentException("");
        return count;
	}
	@Override
	public Integer appendCodeList(CodeSystem cl, boolean all) throws Exception {
        int count = SpringContext.getBean(Store.class).updateCodeList(cl, true, all);
        if (count<=0)
            throw new NoContentException("");
        return count;
	}


    //code
	@Override
	public void updateCode(Code code) throws Exception {
        if (SpringContext.getBean(Store.class).updateCode(code)<=0)
            throw new NoContentException("");
	}

    @Override
    public void updateCodes(CodeSystem cl) throws Exception {
        if (SpringContext.getBean(Store.class).updateCodeListCodes(cl, false)<=0)
            throw new NoContentException("");
    }

    @Override
    public void appendCodes(CodeSystem cl) throws Exception {
        if (SpringContext.getBean(Store.class).updateCodeListCodes(cl, true)<=0)
            throw new NoContentException("");
    }


    //Index
    @Override
    public void rebuildIndex(String system, String version) throws Exception {
        if (SpringContext.getBean(Store.class).codeListIndex(system,version)<=0)
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
