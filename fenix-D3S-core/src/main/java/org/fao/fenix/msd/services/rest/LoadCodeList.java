package org.fao.fenix.msd.services.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dao.cl.CodeListConverter;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.cl.type.CodeRelationshipType;
import org.fao.fenix.msd.services.impl.Load;
import org.fao.fenix.server.tools.spring.SpringContext;
                       
public class LoadCodeList implements org.fao.fenix.msd.services.spi.LoadCodeList {

	@Override
	public Response getCodeList(HttpServletRequest request, String system, String version, Boolean all) {
		try {
			CodeSystem systemValue = SpringContext.getBean(Load.class).getCodeList(system, version, all);
			return systemValue!=null ? Response.ok(systemValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getCodeList(HttpServletRequest request, Boolean all) {
		try {
			Collection<CodeSystem> systemValue = SpringContext.getBean(Load.class).getCodeList(all);
			return systemValue!=null && systemValue.size()>0 ? Response.ok(systemValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@Override
	public Response getCodesLevel(HttpServletRequest request, String system, String version, int level) {
		try
		{				
			Collection<Code> codes = SpringContext.getBean(Load.class).loadCodesLevel(system, version, level);
			return codes!=null && codes.size()>0 ? Response.ok(codes).build() : Response.noContent().build();
		}
		catch (Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@Override
	public Response getCode(HttpServletRequest request,String system, String version, String code, Integer levels)
	{		
		if (levels == null)
			levels = CodeListConverter.ALL_LEVELS;
		try
		{				
			Code branch = SpringContext.getBean(Load.class).loadCode(system, version, code, levels);
			return branch!=null ? Response.ok(branch).build() : Response.noContent().build();
		}
		catch (Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@Override
	public Response getCodes(HttpServletRequest request,String system, String version, Collection<String> codes, Integer levels)
	{
		if (levels == null)
			levels = CodeListConverter.ALL_LEVELS;
		try
		{				
			Collection<Code> toRet = SpringContext.getBean(Load.class).loadCodes(system, version, codes, levels);
			return toRet!=null && toRet.size()>0 ? Response.ok(toRet).build() : Response.noContent().build();
		}
		catch (Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@Override
	public Response getCodesMap(HttpServletRequest request,String system, String version, Integer levels)
	{
		return getCodesMap(request, system, version, null, levels);
	}
	@Override
	public Response getCodesMap(HttpServletRequest request,String system, String version, String code, Integer levels)
	{
		if (levels == null)
			levels = CodeListConverter.ALL_LEVELS;
		try
		{				
			Map<String,Code> toRet = SpringContext.getBean(Load.class).getCodesMap(system, version, code, levels);
			return toRet!=null && toRet.size()>0 ? Response.ok(toRet).build() : Response.noContent().build();
		}
		catch (Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//Same as getCodesMap but takes an array of codes instead of a single code
	@Override
	public Response getCodesListMap(HttpServletRequest request,String system, String version, Integer levels, Collection<String> codes)
	{
		if (levels == null)
			levels = CodeListConverter.ALL_LEVELS;
		try
		{		
			Map<String,Code> toRet = SpringContext.getBean(Load.class).getCodesMap(system, version, codes, levels);
			return toRet!=null && toRet.size()>0 ? Response.ok(toRet).build() : Response.noContent().build();
		}
		catch (Exception e)
		{
			//e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
			
	@Override
	public Response getKeywords(HttpServletRequest request) {
		try {
			Collection<String> systemValue = SpringContext.getBean(Load.class).getKeywords();
			return systemValue!=null && systemValue.size()>0 ? Response.ok(systemValue).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	//relationships
	@Override
	public Response getRelationshipsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
		try {
			Collection<CodeRelationship> result = SpringContext.getBean(Load.class).getRelationships(new CodeSystem(systemFrom, versionFrom));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getRelationshipsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
		try {
			Collection<CodeRelationship> result = SpringContext.getBean(Load.class).getRelationships(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getRelationshipsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
		try {
			Collection<CodeRelationship> result = SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getRelationshipsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
		try {
			Collection<CodeRelationship> result = SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getRelationshipsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
		try {
			Collection<CodeRelationship> result = SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getRelationshipsFromCLT(HttpServletRequest request, String systemFrom, String versionFrom, String typeCode) {
		try {
			Collection<CodeRelationship> result = SpringContext.getBean(Load.class).getRelationships(new CodeSystem(systemFrom, versionFrom), CodeRelationshipType.getByCode(typeCode));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getRelationshipsFromCT(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String typeCode) {
		try {
			Collection<CodeRelationship> result = SpringContext.getBean(Load.class).getRelationships(new Code(systemFrom, versionFrom, codeFrom), CodeRelationshipType.getByCode(typeCode));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//conversions
	@Override
	public Response getConversionsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
		try {
			Collection<CodeConversion> result = SpringContext.getBean(Load.class).getConversions(new CodeSystem(systemFrom, versionFrom));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getConversionsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
		try {
			Collection<CodeConversion> result = SpringContext.getBean(Load.class).getConversions(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getConversionsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
		try {
			Collection<CodeConversion> result = SpringContext.getBean(Load.class).getConversions(new Code(systemFrom, versionFrom, codeFrom));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getConversionsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
		try {
			Collection<CodeConversion> result = SpringContext.getBean(Load.class).getConversions(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getConversionsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
		try {
			Collection<CodeConversion> result = SpringContext.getBean(Load.class).getConversions(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	//propaedeutics
	@Override
	public Response getPropaedeuticsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
		try {
			Collection<CodePropaedeutic> result = SpringContext.getBean(Load.class).getPropaedeutics(new CodeSystem(systemFrom, versionFrom));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getPropaedeuticsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
		try {
			Collection<CodePropaedeutic> result = SpringContext.getBean(Load.class).getPropaedeutics(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getPropaedeuticsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
		try {
			Collection<CodePropaedeutic> result = SpringContext.getBean(Load.class).getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getPropaedeuticsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
		try {
			Collection<CodePropaedeutic> result = SpringContext.getBean(Load.class).getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getPropaedeuticsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
		try {
			Collection<CodePropaedeutic> result = SpringContext.getBean(Load.class).getPropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
			return result!=null && result.size()>0 ? Response.ok(result).build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
