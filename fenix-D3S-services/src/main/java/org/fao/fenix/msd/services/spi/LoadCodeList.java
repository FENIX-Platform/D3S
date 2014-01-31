package org.fao.fenix.msd.services.spi;

import org.fao.fenix.msd.dto.cl.*;
import org.jboss.resteasy.annotations.GZIP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface LoadCodeList {

	@GET
    @GZIP
	@Path("system/{system}/{version}")
	public CodeSystem getCodeList(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
	@GET
    @GZIP
	@Path("system")
	public Collection<CodeSystem> getCodeList(@Context HttpServletRequest request, @QueryParam("all") @DefaultValue("false") Boolean all) throws Exception;
	//Returns all the codes at level 0 as property of an empty "root" Code object 
	@GET
    @GZIP
	@Path("code/{system}/{version}")
	public Collection<Code> getCodesLevel(@Context HttpServletRequest request, @PathParam("system") String system, @PathParam("version") String version, @QueryParam("level") @DefaultValue("1") int level) throws Exception;
	//Returns the code, the children property is filled with the children up to the level passed as param
	@GET
    @GZIP
	@Path("code/{system}/{version}/{code}")
	public Code getCode(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @PathParam("code") String code, @QueryParam("levels") @DefaultValue("0") Integer levels) throws Exception;
	@POST
    @GZIP
	@Path("codes/{system}/{version}")
	public Collection<Code> getCodes(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, Collection<String> codes, @QueryParam("levels") @DefaultValue("0") Integer levels) throws Exception;
		
	//Returns a Map of code-CodeObject
	@GET
    @GZIP
	@Path("codesMap/{system}/{version}")
	public Map<String,Code> getCodesMap(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @QueryParam("levels") @DefaultValue("0") Integer levels) throws Exception;
	@GET
    @GZIP
	@Path("codesMap/{system}/{version}/{code}")
	public Map<String,Code> getCodesMap(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @PathParam("code") String code, @QueryParam("levels") @DefaultValue("0") Integer levels) throws Exception;
	@POST
    @GZIP
	@Path("codesMap/{system}/{version}")
	public Map<String,Code> getCodesListMap(@Context HttpServletRequest request,@PathParam("system") String system, @PathParam("version") String version, @QueryParam("levels") @DefaultValue("0") Integer levels,  Collection<String> codes) throws Exception;
	
	@GET
    @GZIP
	@Path("keywords")
	public Collection<String> getKeywords(@Context HttpServletRequest request) throws Exception;
	
	//relationships
	@GET
    @GZIP
	@Path("relationships/fromCL")
	public Collection<CodeRelationship> getRelationshipsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@GET
    @GZIP
	@Path("relationships/fromCLtoCL")
	public Collection<CodeRelationship> getRelationshipsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
    @GZIP
	@Path("relationships/fromC")
	public Collection<CodeRelationship> getRelationshipsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@GET
    @GZIP
	@Path("relationships/fromCtoC")
	public Collection<CodeRelationship> getRelationshipsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@GET
    @GZIP
	@Path("relationships/fromCtoCL")
	public Collection<CodeRelationship> getRelationshipsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
    @GZIP
	@Path("relationships/fromCLT")
	public Collection<CodeRelationship> getRelationshipsFromCLT(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("type") String typeCode) throws Exception;
	@GET
    @GZIP
	@Path("relationships/fromCT")
	public Collection<CodeRelationship> getRelationshipsFromCT(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("type") String typeCode) throws Exception;
	//conversions
	@GET
    @GZIP
	@Path("conversions/fromCL")
	public Collection<CodeConversion> getConversionsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@GET
    @GZIP
	@Path("conversions/fromCLtoCL")
	public Collection<CodeConversion> getConversionsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
    @GZIP
	@Path("conversions/fromC")
	public Collection<CodeConversion> getConversionsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@GET
    @GZIP
	@Path("conversions/fromCtoC")
	public Collection<CodeConversion> getConversionsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@GET
    @GZIP
	@Path("conversions/fromCtoCL")
	public Collection<CodeConversion> getConversionsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	
	//propaedeutics
	@GET
    @GZIP
	@Path("propaedeutics/fromCL")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@GET
    @GZIP
	@Path("propaedeutics/fromCLtoCL")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCLToCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
    @GZIP
	@Path("propaedeutics/fromC")
	public Collection<CodePropaedeutic> getPropaedeuticsFromC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@GET
    @GZIP
	@Path("propaedeutics/fromCtoC")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoC(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@GET
    @GZIP
	@Path("propaedeutics/fromCtoCL")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoCL(@Context HttpServletRequest request, @QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
}
