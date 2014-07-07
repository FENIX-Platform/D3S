package org.fao.fenix.d3s.msd.services.spi.canc;

import org.fao.fenix.commons.msd.dto.cl.*;
import org.fao.fenix.commons.msd.dto.full.cl.*;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.*;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface LoadCodeList {

	@GET
	@Path("system/{system}/{version}")
	public CodeSystem getCodeList(@PathParam("system") String system, @PathParam("version") String version, @QueryParam("all") @DefaultValue("true") Boolean all) throws Exception;
	@GET
	@Path("system")
	public Collection<CodeSystem> getCodeList(@QueryParam("all") @DefaultValue("false") Boolean all) throws Exception;
	//Returns all the codes at level 0 as property of an empty "root" Code object 
	@GET
	@Path("code/{system}/{version}")
	public Collection<Code> getCodesLevel(@PathParam("system") String system, @PathParam("version") String version, @QueryParam("level") @DefaultValue("1") int level) throws Exception;
	//Returns the code, the children property is filled with the children up to the level passed as param
	@GET
	@Path("code/{system}/{version}/{code}")
	public Code getCode(@PathParam("system") String system, @PathParam("version") String version, @PathParam("code") String code, @QueryParam("levels") @DefaultValue("-1") Integer levels) throws Exception;
    @POST
    @Path("codes/title/{language}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Collection<Code> getCodesByTitle(@PathParam("language") String languageCode, String text) throws Exception;
    @POST
    @Path("systems/title/{language}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Collection<CodeSystem> getCodeListsByTitle(@PathParam("language") String languageCode, String text) throws Exception;
    @POST
    @Path("codes/{system}/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<Code> getCodes(@PathParam("system") String system, @PathParam("version") String version, Collection<String> codes, @QueryParam("levels") @DefaultValue("0") Integer levels) throws Exception;

	//Returns a Map of code-CodeObject
	@GET
	@Path("codesMap/{system}/{version}")
	public Map<String,Code> getCodesMap(@PathParam("system") String system, @PathParam("version") String version, @QueryParam("levels") @DefaultValue("0") Integer levels) throws Exception;
	@GET
	@Path("codesMap/{system}/{version}/{code}")
	public Map<String,Code> getCodesMap(@PathParam("system") String system, @PathParam("version") String version, @PathParam("code") String code, @QueryParam("levels") @DefaultValue("0") Integer levels) throws Exception;
	@POST
	@Path("codesMap/{system}/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
	public Map<String,Code> getCodesListMap(@PathParam("system") String system, @PathParam("version") String version, @QueryParam("levels") @DefaultValue("0") Integer levels,  Collection<String> codes) throws Exception;
	
	@GET
	@Path("keywords")
	public Collection<String> getKeywords() throws Exception;
	
	//relationships
	@GET
	@Path("relationships/fromCL")
	public Collection<CodeRelationship> getRelationshipsFromCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@GET
	@Path("relationships/fromCLtoCL")
	public Collection<CodeRelationship> getRelationshipsFromCLToCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
	@Path("relationships/fromC")
	public Collection<CodeRelationship> getRelationshipsFromC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@GET
	@Path("relationships/fromCtoC")
	public Collection<CodeRelationship> getRelationshipsFromCtoC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@GET
	@Path("relationships/fromCtoCL")
	public Collection<CodeRelationship> getRelationshipsFromCtoCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
	@Path("relationships/fromCLT")
	public Collection<CodeRelationship> getRelationshipsFromCLT(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("type") String typeCode) throws Exception;
	@GET
	@Path("relationships/fromCT")
	public Collection<CodeRelationship> getRelationshipsFromCT(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("type") String typeCode) throws Exception;
	//conversions
	@GET
	@Path("conversions/fromCL")
	public Collection<CodeConversion> getConversionsFromCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@GET
	@Path("conversions/fromCLtoCL")
	public Collection<CodeConversion> getConversionsFromCLToCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
	@Path("conversions/fromC")
	public Collection<CodeConversion> getConversionsFromC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@GET
	@Path("conversions/fromCtoC")
	public Collection<CodeConversion> getConversionsFromCtoC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@GET
	@Path("conversions/fromCtoCL")
	public Collection<CodeConversion> getConversionsFromCtoCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	
	//propaedeutics
	@GET
	@Path("propaedeutics/fromCL")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom) throws Exception;
	@GET
	@Path("propaedeutics/fromCLtoCL")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCLToCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
	@GET
	@Path("propaedeutics/fromC")
	public Collection<CodePropaedeutic> getPropaedeuticsFromC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom) throws Exception;
	@GET
	@Path("propaedeutics/fromCtoC")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoC(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo, @QueryParam("codeTo") String codeTo) throws Exception;
	@GET
	@Path("propaedeutics/fromCtoCL")
	public Collection<CodePropaedeutic> getPropaedeuticsFromCtoCL(@QueryParam("systemFrom") String systemFrom, @QueryParam("versionFrom") String versionFrom, @QueryParam("codeFrom") String codeFrom, @QueryParam("systemTo") String systemTo, @QueryParam("versionTo") String versionTo) throws Exception;
}
