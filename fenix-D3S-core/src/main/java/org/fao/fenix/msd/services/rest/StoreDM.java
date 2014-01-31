package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.msd.services.impl.Delete;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/dm")
public class StoreDM implements org.fao.fenix.msd.services.spi.StoreDM {

	//dataset
	@Override
	public Response newDatasetMetadata(HttpServletRequest request, DM dm) {
		try {
			String uid = SpringContext.getBean(Store.class).newDatasetMetadata(dm);
			return Response.ok(uid).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response updateDatasetMetadata(HttpServletRequest request, DM dm) {
		try {
			int count =	SpringContext.getBean(Store.class).updateDatasetMetadata(dm,false);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response appendDatasetMetadata(HttpServletRequest request, DM dm) {
		try {
			int count =	SpringContext.getBean(Store.class).updateDatasetMetadata(dm,true);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response deleteDatasetMetadata(HttpServletRequest request, String uid) {
		try {
			int count =	SpringContext.getBean(Delete.class).deleteDatasetMetadata(uid);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response indexDatasetMetadata(HttpServletRequest request, String uid) {
		try {
			int count =	SpringContext.getBean(Store.class).datasetIndex(uid);
			return count>0 ? Response.ok().build() : Response.noContent().build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
    @Override
    public Response indexDatasetsRebuild(@Context HttpServletRequest request) {
        try {
            int count =	SpringContext.getBean(Store.class).datasetIndex(null);
            return count>0 ? Response.ok().build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    //structure
    @Override
    public Response newMetadataStructure(HttpServletRequest request, DMMeta mm) {
        try {
            String uid = SpringContext.getBean(Store.class).newMetadataStructure(mm);
            return Response.ok(uid).build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response updateMetadataStructure(HttpServletRequest request, DMMeta mm) {
        try {
            int count =	SpringContext.getBean(Store.class).updateMetadataStructure(mm,false);
            return count>0 ? Response.ok().build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response appendMetadataStructure(HttpServletRequest request, DMMeta mm) {
        try {
            int count =	SpringContext.getBean(Store.class).updateMetadataStructure(mm,true);
            return count>0 ? Response.ok().build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @Override
    public Response deleteMetadataStructure(HttpServletRequest request, String uid) {
        try {
            int count =	SpringContext.getBean(Delete.class).deleteMetadataStructure(uid);
            return count>0 ? Response.ok().build() : Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }


    //Associate a dataset to one or more categories
	@Override
	public Response addCategoriesToDataset(HttpServletRequest request, String uid, Collection<Code> listOfCodes)
	{
		try
		{
			int count = SpringContext.getBean(Store.class).addCategoriesToDataset(uid, listOfCodes);
			return count>0? Response.ok().build() : Response.noContent().build();
		}
		catch (Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
