package org.fao.fenix.msd.services.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.server.services.rest.Service;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Path("msd/cl")
public class LoadCodeList extends Service implements org.fao.fenix.msd.services.spi.LoadCodeList {

	@Override
	public Response getCodeList(HttpServletRequest request, String system, String version, Boolean all) {
        return defaultCall(request, CodeSystem.class, system, version, all);
	}
	@Override
    public Collection<CodeSystem> getCodeList(HttpServletRequest request, Boolean all) throws Exception {
        try {
            return getProxy(org.fao.fenix.msd.services.spi.LoadCodeList.class).getCodeList(request,all);
        } catch (InternalServerErrorException ex) {
            throw new Exception("Origin server error: ("+ex.getMessage()+") "+ex.getResponse().readEntity(String.class));
        }
    }
	
	@Override
	public Response getKeywords(HttpServletRequest request) {
        return defaultCall(request, Collection.class);
	}
    @Override
    public Response getCodesLevel(HttpServletRequest request, String system, String version, int level) {
        return defaultCall(request, Collection.class, system, version, level);
    }

	@Override
	public Response getCode(HttpServletRequest request, String system, String version, String code, Integer levels) {
        return defaultCall(request, Code.class, system, version, code, levels);
	}

    @Override
    public Response getCodes(HttpServletRequest request, String system, String version, Collection<String> codes, Integer levels) {
        return defaultCall(request, Collection.class, system, version, codes, levels);
    }


    @Override
	public Response getCodesMap(HttpServletRequest request, String system, String version, Integer levels) {
        return defaultCall(request, HashMap.class, system, version, levels);
	}
	@Override
	public Response getCodesMap(HttpServletRequest request, String system, String version, String code, Integer levels) {
        return defaultCall(request, HashMap.class, system, version, code, levels);
	}

    @Override
    public Response getCodesListMap(HttpServletRequest request, String system, String version, Integer levels, Collection<String> codes) {
        return defaultCall(request, java.util.Map.class, system, version, levels, codes);
    }
 /*
    //relationships
    @Override
    public Response getCodesListMap(HttpServletRequest request, String system, String version, Integer levels, String[] codes) {
        return defaultCall(request, HashMap.class, system, version, levels, codes);
    }  */

	@Override
	public Response getRelationshipsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom);
	}
	@Override
	public Response getRelationshipsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Response getRelationshipsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Response getRelationshipsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Response getRelationshipsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}
	@Override
	public Response getRelationshipsFromCLT(HttpServletRequest request, String systemFrom, String versionFrom, String typeCode) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, typeCode);
	}
	@Override
	public Response getRelationshipsFromCT(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String typeCode) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom, typeCode);
	}
	//conversions
	@Override
	public Response getConversionsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom);
	}
	@Override
	public Response getConversionsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Response getConversionsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Response getConversionsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Response getConversionsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}
	
	//propaedeutics
	@Override
	public Response getPropaedeuticsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom);
	}
	@Override
	public Response getPropaedeuticsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, systemTo, versionTo);
	}
	@Override
	public Response getPropaedeuticsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom);
	}
	@Override
	public Response getPropaedeuticsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom, systemTo, versionTo, codeTo);
	}
	@Override
	public Response getPropaedeuticsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
        return defaultCall(request, Collection.class, systemFrom, versionFrom, codeFrom, systemTo, versionTo);
	}

}
