package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

public class StoreCodeList implements org.fao.fenix.msd.services.spi.StoreCodeList {

	//code list
	@Override
	public Response newCodeList(HttpServletRequest request, CodeSystem cl) {
		try {
			SpringContext.getBean(Store.class).newCodeList(cl);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response updateCodeList(HttpServletRequest request, CodeSystem cl) {
		try {
			int count =	SpringContext.getBean(Store.class).updateCodeList(cl,true);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response appendCodeList(HttpServletRequest request, CodeSystem cl) {
		try {
			int count =	SpringContext.getBean(Store.class).updateCodeList(cl,true);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
    @Override
    public Response restoreCodeList(HttpServletRequest request) {
        try {
            int count =	SpringContext.getBean(Store.class).codeListIndex(null, null);
            return count>0 ? Response.ok(count).build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    @Override
    public Response restoreCodeList(HttpServletRequest request, String system, String version) {
        try {
            int count =	SpringContext.getBean(Store.class).codeListIndex(system, version);
            return count>0 ? Response.ok(count).build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    //code
	@Override
	public Response updateCode(HttpServletRequest request, Code code) {
		try {
			int count =	SpringContext.getBean(Store.class).updateCode(code);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	

	//keyword
	@Override
	public Response newKeyword(HttpServletRequest request, @PathParam("keyword") String keyword) {
		try {
			SpringContext.getBean(Store.class).newKeyword(keyword);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	//relationship
	@Override
	public Response newRelationship(HttpServletRequest request, CodeRelationship relation) {
		try {
			SpringContext.getBean(Store.class).newRelationship(relation);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response newRelationship(HttpServletRequest request, Collection<CodeRelationship> relation) {
		try {
			SpringContext.getBean(Store.class).newRelationship(relation);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//conversion
	@Override
	public Response newConversion(HttpServletRequest request, CodeConversion conversion) {
		try {
			SpringContext.getBean(Store.class).newConversion(conversion);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response newConversion(HttpServletRequest request, Collection<CodeConversion> conversion) {
		try {
			SpringContext.getBean(Store.class).newConversion(conversion);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response updateConversion(HttpServletRequest request, CodeConversion conversion) {
		try {
			int count =	SpringContext.getBean(Store.class).updateConversion(conversion);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	//propaedeutic
	@Override
	public Response newPropaedeutic(HttpServletRequest request, CodePropaedeutic propaedeutic) {
		try {
			SpringContext.getBean(Store.class).newPropaedeutic(propaedeutic);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response newPropaedeutic(HttpServletRequest request, Collection<CodePropaedeutic> propaedeutic) {
		try {
			SpringContext.getBean(Store.class).newPropaedeutic(propaedeutic);
			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}
