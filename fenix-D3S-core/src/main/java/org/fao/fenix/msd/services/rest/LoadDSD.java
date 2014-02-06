package org.fao.fenix.msd.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.msd.services.impl.Load;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("msd/dsd")
public class LoadDSD implements org.fao.fenix.msd.services.spi.LoadDSD {
    @Context HttpServletRequest request;

	@Override
	public Collection<DSDDatasource> getDatasources() throws Exception {
        return SpringContext.getBean(Load.class).getDatasources();
	}
	@Override
	public Collection<DSDDimension> getDimensions() throws Exception {
        return SpringContext.getBean(Load.class).getDimensions();
	}
	@Override
	public Collection<DSDContextSystem> getContextSystems() throws Exception {
		return SpringContext.getBean(Load.class).getContextSystems();
	}
	
}
