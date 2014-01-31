package org.fao.fenix.msd.services.rest;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;

import org.fao.fenix.msd.dto.cl.*;
import org.fao.fenix.server.services.rest.Service;

@Path("msd/cl")
public class LoadCodeList extends Service implements org.fao.fenix.msd.services.spi.LoadCodeList {

	@Override
	public CodeSystem getCodeList(HttpServletRequest request, String system, String version, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodeList(request, system, version, all);
	}
	@Override
    public Collection<CodeSystem> getCodeList(HttpServletRequest request, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodeList(request,all);
    }
	
	@Override
	public Collection<String> getKeywords(HttpServletRequest request) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getKeywords(request);
	}
    @Override
    public Collection<Code> getCodesLevel(HttpServletRequest request, String system, String version, int level) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesLevel(request, system, version, level);
    }

	@Override
	public Code getCode(HttpServletRequest request, String system, String version, String code, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCode(request, system, version, code, levels);
	}

    @Override
    public Collection<Code> getCodes(HttpServletRequest request, String system, String version, Collection<String> codes, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodes(request, system, version, codes, levels);
    }


    @Override
	public Map<String, Code> getCodesMap(HttpServletRequest request, String system, String version, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesMap(request, system, version, levels);
	}
	@Override
	public Map<String, Code> getCodesMap(HttpServletRequest request, String system, String version, String code, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesMap(request, system, version, code, levels);
	}

    @Override
    public Map<String, Code> getCodesListMap(HttpServletRequest request, String system, String version, Integer levels, Collection<String> codes) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesListMap(request, system, version, levels, codes);
    }

	@Override
	public Collection<CodeRelationship> getRelationshipsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCL(request, systemFrom, versionFrom);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCLToCL(request, systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromC(request, systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCtoC(request, systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCtoCL(request, systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLT(HttpServletRequest request, String systemFrom, String versionFrom, String typeCode) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCLT(request, systemFrom, versionFrom, typeCode);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCT(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String typeCode) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCT(request, systemFrom, versionFrom, codeFrom, typeCode);
	}
	//conversions
	@Override
	public Collection<CodeConversion> getConversionsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCL(request, systemFrom, versionFrom);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCLToCL(request, systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromC(request, systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCtoC(request, systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCtoCL(request, systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}
	
	//propaedeutics
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCL(request, systemFrom, versionFrom);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCLToCL(request, systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromC(request, systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCtoC(request, systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCtoCL(request, systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}

}
