package org.fao.fenix.msd.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
	public Response deleteCodeList(HttpServletRequest request, String system, String version) {
		try {
			int count =	SpringContext.getBean(Delete.class).deleteCodeList(system, version);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@Override
	public Response deleteKeyword(HttpServletRequest request, String keyword) {
		try {
			int count =	SpringContext.getBean(Delete.class).deleteKeyword(keyword);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//relationships
	@Override
	public Response deleteRelationshipsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteRelationships(new CodeRelationship(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteRelationshipsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteRelationships(new CodeSystem(systemFrom, versionFrom));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteRelationshipsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteRelationships(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteRelationshipsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteRelationships(new Code(systemFrom, versionFrom, codeFrom));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteRelationshipsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteRelationships(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//conversions
	@Override
	public Response deleteConversionsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteConversions(new CodeConversion(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteConversionsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteConversions(new CodeSystem(systemFrom, versionFrom));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteConversionsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteConversions(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteConversionsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteConversions(new Code(systemFrom, versionFrom, codeFrom));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteConversionsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deleteConversions(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//propaedeutics
	@Override
	public Response deletePropaedeuticsFromCtoC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo, String codeTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deletePropaedeutics(new CodePropaedeutic(new Code(systemFrom, versionFrom, codeFrom), new Code(systemTo, versionTo, codeTo)));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deletePropaedeuticsFromCL(HttpServletRequest request, String systemFrom, String versionFrom) {
		try {
			int count = SpringContext.getBean(Delete.class).deletePropaedeutics(new CodeSystem(systemFrom, versionFrom));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deletePropaedeuticsFromCLToCL(HttpServletRequest request, String systemFrom, String versionFrom, String systemTo, String versionTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deletePropaedeutics(new CodeSystem(systemFrom, versionFrom), new CodeSystem(systemTo, versionTo));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deletePropaedeuticsFromC(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom) {
		try {
			int count = SpringContext.getBean(Delete.class).deletePropaedeutics(new Code(systemFrom, versionFrom, codeFrom));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deletePropaedeuticsFromCtoCL(HttpServletRequest request, String systemFrom, String versionFrom, String codeFrom, String systemTo, String versionTo) {
		try {
			int count = SpringContext.getBean(Delete.class).deletePropaedeutics(new Code(systemFrom, versionFrom, codeFrom), new CodeSystem(systemTo, versionTo));
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
}
