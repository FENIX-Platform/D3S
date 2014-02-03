package org.fao.fenix.msd.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.services.impl.Delete;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/cl")
public class DeleteCodeList implements org.fao.fenix.msd.services.spi.DeleteCodeList {

	@Override
	public void deleteCodeList(HttpServletRequest request, String system, String version) throws Exception {
        SpringContext.getBean(Delete.class).deleteCodeList(system, version);
	}
	
	@Override
	public void deleteKeyword(HttpServletRequest request, String keyword) throws Exception {
		SpringContext.getBean(Delete.class).deleteKeyword(keyword);
	}
	//relationships
	@Override
	public void deleteRelationshipsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
		SpringContext.getBean(Delete.class).deleteRelationships(new CodeRelationship(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)));
	}
	@Override
	public void deleteRelationshipsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
		SpringContext.getBean(Delete.class).deleteRelationships(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public void deleteRelationshipsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
		SpringContext.getBean(Delete.class).deleteRelationships(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public void deleteRelationshipsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
		SpringContext.getBean(Delete.class).deleteRelationships(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public void deleteRelationshipsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
		SpringContext.getBean(Delete.class).deleteRelationships(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
	//conversions
	@Override
	public void deleteConversionsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
		SpringContext.getBean(Delete.class).deleteConversions(new CodeConversion(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)));
	}
	@Override
	public void deleteConversionsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
		SpringContext.getBean(Delete.class).deleteConversions(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public void deleteConversionsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
		SpringContext.getBean(Delete.class).deleteConversions(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public void deleteConversionsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
		SpringContext.getBean(Delete.class).deleteConversions(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public void deleteConversionsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
		SpringContext.getBean(Delete.class).deleteConversions(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
	//propaedeutics
	@Override
	public void deletePropaedeuticsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
		SpringContext.getBean(Delete.class).deletePropaedeutics(new CodePropaedeutic(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)));
	}
	@Override
	public void deletePropaedeuticsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) throws Exception {
		SpringContext.getBean(Delete.class).deletePropaedeutics(new CodeSystem(systemFrom, versionFrom));
	}
	@Override
	public void deletePropaedeuticsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
		SpringContext.getBean(Delete.class).deletePropaedeutics(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
	}
	@Override
	public void deletePropaedeuticsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) throws Exception {
		SpringContext.getBean(Delete.class).deletePropaedeutics(new Code(systemFrom, versionFrom, codeFrom));
	}
	@Override
	public void deletePropaedeuticsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
		SpringContext.getBean(Delete.class).deletePropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
	}
	
}
