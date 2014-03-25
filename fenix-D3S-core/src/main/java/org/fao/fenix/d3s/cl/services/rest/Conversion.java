package org.fao.fenix.d3s.cl.services.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.d3s.cl.dto.ConversionParameters;
import org.fao.fenix.d3s.cl.dto.Value;
import org.fao.fenix.d3s.cl.services.impl.ConversionByCodeList;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

@Path("cl/conversion")
public class Conversion implements org.fao.fenix.d3s.cl.services.spi.Conversion {
    @Context HttpServletRequest request;

	@Override
	public Value getValueConversion(ConversionParameters conversionInfo) throws Exception {
		return SpringContext.getBean(ConversionByCodeList.class).applyConversion(conversionInfo.getValue(), conversionInfo.getUnitOfMeasure());
	}

}