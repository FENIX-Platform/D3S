package org.fao.fenix.cl.services.spi;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fao.fenix.cl.dto.ConversionParameters;
import org.fao.fenix.cl.dto.Value;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public interface Conversion {

	@GET
	public Value getValueConversion(ConversionParameters conversionInfo) throws Exception;

}
