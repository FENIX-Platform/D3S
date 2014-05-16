package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.d3s.msd.dao.common.MSDDao;
import org.fao.fenix.commons.msd.dto.common.Select;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.Collection;
import java.util.Map;

@Path("msd")
public class MSD implements org.fao.fenix.d3s.msd.services.spi.MSD {
    @Context HttpServletRequest request;
    @Inject private MSDDao dao;

    @Override
    public Collection<Map<String,Object>> select(Select queryInfo) throws Exception {
        return dao.select(queryInfo);
    }
}
