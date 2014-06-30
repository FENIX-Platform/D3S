package org.fao.fenix.d3s.msd.services.rest;

import java.util.Collection;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDDatasource;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDDimension;
import org.fao.fenix.d3s.msd.services.impl.Load;

@Path("msd/dsd")
public class LoadDSD implements org.fao.fenix.d3s.msd.services.spi.LoadDSD {
    @Context HttpServletRequest request;
    @Inject private Load load;

    @Override
	public Collection<DSDDatasource> getDatasources() throws Exception {
        return load.getDatasources();
	}
	@Override
	public Collection<DSDDimension> getDimensions() throws Exception {
        return load.getDimensions();
	}
	@Override
	public Collection<DSDContextSystem> getContextSystems() throws Exception {
		return load.getContextSystems();
	}
	
}
