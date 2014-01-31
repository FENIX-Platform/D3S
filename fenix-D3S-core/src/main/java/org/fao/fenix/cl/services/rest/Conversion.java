package org.fao.fenix.cl.services.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.cl.dto.ConversionParameters;
import org.fao.fenix.cl.dto.Value;
import org.fao.fenix.cl.services.impl.ConversionByCodeList;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("cl/conversion")
public class Conversion implements org.fao.fenix.cl.services.spi.Conversion {

	@Override
	public Response getValueConversion(ConversionParameters conversionInfo) {
		try {
			Value converted = SpringContext.getBean(ConversionByCodeList.class).applyConversion(conversionInfo.getValue(), conversionInfo.getUnitOfMeasure());
			return Response.ok(converted).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}
