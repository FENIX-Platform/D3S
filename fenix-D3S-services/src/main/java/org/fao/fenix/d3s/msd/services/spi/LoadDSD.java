package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.full.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.full.dsd.DSDDimension;
import org.fao.fenix.commons.msd.dto.full.dsd.DSDDatasource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes
public interface LoadDSD {

	@GET
	@Path("datasource")
	public Collection<DSDDatasource> getDatasources() throws Exception;
	@GET
	@Path("dimension")
	public Collection<DSDDimension> getDimensions() throws Exception;
	@GET
	@Path("context")
	public Collection<DSDContextSystem> getContextSystems() throws Exception;
	
}
