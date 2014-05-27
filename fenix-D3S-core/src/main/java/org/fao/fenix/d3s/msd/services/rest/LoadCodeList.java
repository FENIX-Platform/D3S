package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.full.cl.Code;
import org.fao.fenix.commons.msd.dto.full.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.full.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.full.cl.CodeRelationship;
import org.fao.fenix.commons.msd.dto.full.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.type.cl.CodeRelationshipType;
import org.fao.fenix.d3s.msd.services.impl.Load;
import org.fao.fenix.d3s.server.tools.SupportedLanguages;

@Path("msd/cl")
public class LoadCodeList implements org.fao.fenix.d3s.msd.services.spi.LoadCodeList {
    @Context HttpServletRequest request;
    @Inject private Load load;

	@Override
	public CodeSystem getCodeList(String system, String version, Boolean all) throws Exception {
        return load.getCodeList(system, version, all);
	}
	@Override
	public Collection<CodeSystem> getCodeList(Boolean all) throws Exception {
        return load.getCodeList(all);
	}
	
	@Override
	public Collection<Code> getCodesLevel(String system, String version, int level) throws Exception {
		return load.loadCodesLevel(system, version, level);
	}
	
	@Override
	public Code getCode(String system, String version, String code, Integer levels) throws Exception {
        return load.loadCode(system, version, code, levels);
	}
	
	@Override
	public Collection<Code> getCodes(String system, String version, Collection<String> codes, Integer levels) throws Exception {
		return load.loadCodes(system, version, codes, levels);
	}
	
	@Override
	public Map<String,Code> getCodesMap(String system, String version, Integer levels) throws Exception {
		return getCodesMap(system, version, null, levels);
	}
	@Override
	public Map<String,Code> getCodesMap(String system, String version, String code, Integer levels) throws Exception {
		return load.getCodesMap(system, version, code, levels);
	}

    @Override
    public Collection<Code> getCodesByTitle(String languageCode, String text) throws Exception {
        return load.loadCodes(text, SupportedLanguages.getInstance(languageCode));
    }

    @Override
    public Collection<CodeSystem> getCodeListsByTitle(String languageCode, String text) throws Exception {
        return load.loadCodeLists(text, SupportedLanguages.getInstance(languageCode));
    }

    //Same as getCodesMap but takes an array of codes instead of a single code
	@Override
	public Map<String,Code> getCodesListMap(String system, String version, Integer levels, Collection<String> codes) throws Exception {
		return load.getCodesMap(system, version, codes, levels);
	}
			
	@Override
	public Collection<String> getKeywords() throws Exception {
		return load.getKeywords();
	}
	
	//relationships
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCL(String systemFrom, String versionFrom) throws Exception {
		return load.getRelationships(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
		return load.getRelationships(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
		return load.getRelationships(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
		return load.getRelationships(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
		return load.getRelationships(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLT(String systemFrom, String versionFrom, String typeCode) throws Exception {
		return load.getRelationships(new CodeSystem(systemFrom, versionFrom), CodeRelationshipType.getByCode(typeCode));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCT(String systemFrom, String versionFrom, String codeFrom, String typeCode) throws Exception {
		return load.getRelationships(new Code(systemFrom, versionFrom, codeFrom), CodeRelationshipType.getByCode(typeCode));
	}
	//conversions
	@Override
	public Collection<CodeConversion> getConversionsFromCL(String systemFrom, String versionFrom) throws Exception {
		return load.getConversions(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
		return load.getConversions(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
		return load.getConversions(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
		return load.getConversions(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
		return load.getConversions(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
	
	//propaedeutics
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCL(String systemFrom, String versionFrom) throws Exception {
        return load.getPropaedeutics(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return load.getPropaedeutics(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return load.getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return load.getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return load.getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
}
