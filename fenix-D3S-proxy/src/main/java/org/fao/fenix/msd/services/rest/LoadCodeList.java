package org.fao.fenix.msd.services.rest;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;

import org.fao.fenix.msd.dto.cl.*;
import org.fao.fenix.server.services.rest.Service;

@Path("msd/cl")
public class LoadCodeList extends Service implements org.fao.fenix.msd.services.spi.LoadCodeList {
    @Context HttpServletRequest request;

	@Override
	public CodeSystem getCodeList(String system, String version, Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodeList(system, version, all);
	}
	@Override
    public Collection<CodeSystem> getCodeList(Boolean all) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodeList(all);
    }
	
	@Override
	public Collection<String> getKeywords() throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getKeywords();
	}
    @Override
    public Collection<Code> getCodesLevel(String system, String version, int level) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesLevel(system, version, level);
    }

	@Override
	public Code getCode(String system, String version, String code, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCode(system, version, code, levels);
	}

    @Override
    public Collection<Code> getCodes(String system, String version, Collection<String> codes, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodes(system, version, codes, levels);
    }


    @Override
	public Map<String, Code> getCodesMap(String system, String version, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesMap(system, version, levels);
	}
	@Override
	public Map<String, Code> getCodesMap(String system, String version, String code, Integer levels) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesMap(system, version, code, levels);
	}

    @Override
    public Map<String, Code> getCodesListMap(String system, String version, Integer levels, Collection<String> codes) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodesListMap(system, version, levels, codes);
    }

	@Override
	public Collection<CodeRelationship> getRelationshipsFromCL(String systemFrom, String versionFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCL(systemFrom, versionFrom);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCLToCL(systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromC(systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCtoC(systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCtoCL(systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLT(String systemFrom, String versionFrom, String typeCode) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCLT(systemFrom, versionFrom, typeCode);
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCT(String systemFrom, String versionFrom, String codeFrom, String typeCode) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getRelationshipsFromCT(systemFrom, versionFrom, codeFrom, typeCode);
	}
	//conversions
	@Override
	public Collection<CodeConversion> getConversionsFromCL(String systemFrom, String versionFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCL(systemFrom, versionFrom);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCLToCL(systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromC(systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCtoC(systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getConversionsFromCtoCL(systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}
	
	//propaedeutics
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCL(String systemFrom, String versionFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCL(systemFrom, versionFrom);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCLToCL(systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromC(systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCtoC(systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getPropaedeuticsFromCtoCL(systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}

}
