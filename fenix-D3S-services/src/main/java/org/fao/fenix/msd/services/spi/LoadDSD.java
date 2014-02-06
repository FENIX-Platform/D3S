package org.fao.fenix.msd.services.spi;

import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.jboss.resteasy.annotations.GZIP;

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
    @GZIP
	@Path("datasource")
	public Collection<DSDDatasource> getDatasources() throws Exception;
	@GET
    @GZIP
	@Path("dimension")
	public Collection<DSDDimension> getDimensions() throws Exception;
	@GET
    @GZIP
	@Path("context")
	public Collection<DSDContextSystem> getContextSystems() throws Exception;
	
}
