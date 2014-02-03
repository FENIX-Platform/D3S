package org.fao.fenix.msd.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.msd.services.impl.Delete;
import org.fao.fenix.msd.services.impl.Store;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/dsd")
public class StoreDSD implements org.fao.fenix.msd.services.spi.StoreDSD {

	//dimension
	@Override
	public void newDimension(@Context HttpServletRequest request, DSDDimension dimension) throws Exception {
		SpringContext.getBean(Store.class).newDimension(dimension);
	}
	@Override
	public void updateDimension(@Context HttpServletRequest request, DSDDimension dimension) throws Exception {
		if (SpringContext.getBean(Store.class).updateDimension(dimension)<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteDimension(@Context HttpServletRequest request, @PathParam("name") String name) throws Exception {
        if (SpringContext.getBean(Delete.class).deleteDimension(name)<=0)
            throw new NoContentException("");
	}

	//context system
	@Override
	public void newContextSystem(@Context HttpServletRequest request, DSDContextSystem context) throws Exception {
		SpringContext.getBean(Store.class).newContextSystem(context);
	}
	@Override
	public void deleteContextSystem(@Context HttpServletRequest request, @PathParam("name") String name) throws Exception {
        if (SpringContext.getBean(Delete.class).deleteContextSystem(name)<=0)
            throw new NoContentException("");
	}
	
	//column
	@Override
	public void updateColumn(@Context HttpServletRequest request, @PathParam("datasetUID") String uid, DSDColumn column) throws Exception {
        if (SpringContext.getBean(Store.class).updateColumn(uid, column)<=0)
            throw new NoContentException("");
    }
	
	
}
