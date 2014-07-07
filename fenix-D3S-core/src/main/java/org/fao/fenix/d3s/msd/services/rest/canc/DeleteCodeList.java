package org.fao.fenix.d3s.msd.services.rest.canc;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.commons.msd.dto.templates.canc.cl.Code;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeConversion;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodePropaedeutic;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeRelationship;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeSystem;
import org.fao.fenix.d3s.msd.services.impl.Delete;

@Path("msd/cl")
public class DeleteCodeList implements org.fao.fenix.d3s.msd.services.spi.canc.DeleteCodeList {
    @Context HttpServletRequest request;
    @Inject private Delete delete;

	@Override
	public Integer deleteCodeList(String system, String version) throws Exception {
        int count = delete.deleteCodeList(system, version);
        if (count<=0)
            throw new NoContentException("");
        return count;
	}
	
	@Override
	public void deleteKeyword(String keyword) throws Exception {
        if (delete.deleteKeyword(keyword)<=0)
            throw new NoContentException("");
	}
	//relationships
	@Override
	public void deleteRelationshipsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        if (delete.deleteRelationships(new CodeRelationship(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteRelationshipsFromCL(String systemFrom, String versionFrom) throws Exception {
        if (delete.deleteRelationships(new CodeSystem(systemFrom, versionFrom))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteRelationshipsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        if (delete.deleteRelationships(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteRelationshipsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
        if (delete.deleteRelationships(new Code(systemFrom, versionFrom, codeFrom))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteRelationshipsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        if (delete.deleteRelationships(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo))<=0)
            throw new NoContentException("");
	}
	//conversions
	@Override
	public void deleteConversionsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        if (delete.deleteConversions(new CodeConversion(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteConversionsFromCL(String systemFrom, String versionFrom) throws Exception {
        if (delete.deleteConversions(new CodeSystem(systemFrom, versionFrom))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteConversionsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        if (delete.deleteConversions(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteConversionsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
        if (delete.deleteConversions(new Code(systemFrom, versionFrom, codeFrom))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteConversionsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        if (delete.deleteConversions(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo))<=0)
            throw new NoContentException("");
	}
	//propaedeutics
	@Override
	public void deletePropaedeuticsFromCtoC(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) throws Exception {
        if (delete.deletePropaedeutics(new CodePropaedeutic(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deletePropaedeuticsFromCL(String systemFrom, String versionFrom) throws Exception {
        if (delete.deletePropaedeutics(new CodeSystem(systemFrom, versionFrom))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deletePropaedeuticsFromCLToCL(String systemFrom, String versionFrom, String systemTo, String versionTo) throws Exception {
        if (delete.deletePropaedeutics(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deletePropaedeuticsFromC(String systemFrom, String versionFrom, String codeFrom) throws Exception {
        if (delete.deletePropaedeutics(new Code(systemFrom, versionFrom, codeFrom))<=0)
            throw new NoContentException("");
	}
	@Override
	public void deletePropaedeuticsFromCtoCL(String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) throws Exception {
        if (delete.deletePropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo))<=0)
            throw new NoContentException("");
	}
	
}
