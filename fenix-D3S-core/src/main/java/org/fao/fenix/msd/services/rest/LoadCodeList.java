package org.fao.fenix.msd.services.rest;

import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.cl.type.CodeRelationshipType;
import org.fao.fenix.msd.services.impl.Load;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/cl")
public class LoadCodeList implements org.fao.fenix.msd.services.spi.LoadCodeList {

	@Override
	public CodeSystem getCodeList(HttpServletRequest request, String system, String version, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getCodeList(system, version, all);
	}
	@Override
	public Collection<CodeSystem> getCodeList(HttpServletRequest request, Boolean all) throws Exception {
        return SpringContext.getBean(Load.class).getCodeList(all);
	}
	
	@Override
	public Collection<Code> getCodesLevel(HttpServletRequest request, String system, String version, int level) throws Exception {
		return SpringContext.getBean(Load.class).loadCodesLevel(system, version, level);
	}
	
	@Override
	public Code getCode(HttpServletRequest request,String system, String version, String code, Integer levels) throws Exception {
        return SpringContext.getBean(Load.class).loadCode(system, version, code, levels);
	}
	
	@Override
	public Collection<Code> getCodes(HttpServletRequest request,String system, String version, Collection<String> codes, Integer levels) throws Exception {
		return SpringContext.getBean(Load.class).loadCodes(system, version, codes, levels);
	}
	
	@Override
	public Map<String,Code> getCodesMap(HttpServletRequest request,String system, String version, Integer levels) throws Exception {
		return getCodesMap(request, system, version, null, levels);
	}
	@Override
	public Map<String,Code> getCodesMap(HttpServletRequest request,String system, String version, String code, Integer levels) throws Exception {
		return SpringContext.getBean(Load.class).getCodesMap(system, version, code, levels);
	}
	//Same as getCodesMap but takes an array of codes instead of a single code
	@Override
	public Map<String,Code> getCodesListMap(HttpServletRequest request,String system, String version, Integer levels, Collection<String> codes) throws Exception {
		return SpringContext.getBean(Load.class).getCodesMap(system, version, codes, levels);
	}
			
	@Override
	public Collection<String> getKeywords(HttpServletRequest request) throws Exception {
		return SpringContext.getBean(Load.class).getKeywords();
	}
	
	//relationships
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
		return SpringContext.getBean(Load.class).getRelationships(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
		return SpringContext.getBean(Load.class).getRelationships(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
		return SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
		return SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
		return SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCLT(HttpServletRequest request, String systemFrom, String versionFrom, String typeCode) throws Exception {
		return SpringContext.getBean(Load.class).getRelationships(new CodeSystem(systemFrom, versionFrom), CodeRelationshipType.getByCode(typeCode));
	}
	@Override
	public Collection<CodeRelationship> getRelationshipsFromCT(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String typeCode) throws Exception {
		return SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom), CodeRelationshipType.getByCode(typeCode));
	}
	//conversions
	@Override
	public Collection<CodeConversion> getConversionsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
		return SpringContext.getBean(Load.class).getConversions(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
		return SpringContext.getBean(Load.class).getConversions(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
		return SpringContext.getBean(Load.class).getConversions(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
		return SpringContext.getBean(Load.class).getConversions(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
	}
	@Override
	public Collection<CodeConversion> getConversionsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
		return SpringContext.getBean(Load.class).getConversions(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
	
	//propaedeutics
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
        return SpringContext.getBean(Load.class).getPropaedeutics(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        return SpringContext.getBean(Load.class).getPropaedeutics(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
        return SpringContext.getBean(Load.class).getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        return SpringContext.getBean(Load.class).getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
	}
	@Override
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        return SpringContext.getBean(Load.class).getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
}
