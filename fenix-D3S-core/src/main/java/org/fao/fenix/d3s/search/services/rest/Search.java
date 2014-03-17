package org.fao.fenix.d3s.search.services.rest;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.commons.msd.dto.dsd.DSD;
import org.fao.fenix.commons.msd.dto.dsd.DSDColumn;
import org.fao.fenix.commons.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.dsd.DSDDimension;
import org.fao.fenix.commons.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.commons.search.dto.Response;
import org.fao.fenix.commons.search.dto.filter.Filter;
import org.fao.fenix.commons.search.dto.filter.RequiredPlugin;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.dto.*;
import org.fao.fenix.d3s.search.services.impl.BasicDataSearch;
import org.fao.fenix.d3s.search.services.impl.BasicMetadataSearch;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.fao.fenix.commons.utils.JSONUtils;

@Path("find")
public class Search implements org.fao.fenix.d3s.search.services.spi.Search {
    @Context HttpServletRequest request;

    @Override
	public Response getDataBasicAlgorithm(Filter filter) throws Exception {
        return SpringContext.getBean(BasicDataSearch.class).search(filter);
	}

	@Override
	public Response getMetadataBasicAlgorithm(Filter filter) throws Exception {
        return SpringContext.getBean(BasicMetadataSearch.class).search(filter);
	}



}
