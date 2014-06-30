package org.fao.fenix.d3s.msd.services.rest;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NoContentException;

import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDColumn;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDDimension;
import org.fao.fenix.d3s.msd.services.impl.Store;
import org.fao.fenix.d3s.msd.services.impl.Delete;

@Path("msd/dsd")
public class StoreDSD implements org.fao.fenix.d3s.msd.services.spi.StoreDSD {
    @Context HttpServletRequest request;
    @Inject private Store store;
    @Inject private Delete delete;

    //dimension
	@Override
	public void newDimension(DSDDimension dimension) throws Exception {
		store.newDimension(dimension);
	}
	@Override
	public void updateDimension(DSDDimension dimension) throws Exception {
		if (store.updateDimension(dimension)<=0)
            throw new NoContentException("");
	}
	@Override
	public void deleteDimension(String name) throws Exception {
        if (delete.deleteDimension(name)<=0)
            throw new NoContentException("");
	}

	//context system
	@Override
	public void newContextSystem(DSDContextSystem context) throws Exception {
		store.newContextSystem(context);
	}
	@Override
	public void deleteContextSystem(String name) throws Exception {
        if (delete.deleteContextSystem(name)<=0)
            throw new NoContentException("");
	}
	
	//column
	@Override
	public void updateColumn(String uid, DSDColumn column) throws Exception {
        if (store.updateColumn(uid, column)<=0)
            throw new NoContentException("");
    }
	
	
}
