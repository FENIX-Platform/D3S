package org.fao.fenix.msd.services.rest;

import org.fao.fenix.msd.dao.common.MSDDao;
import org.fao.fenix.msd.dto.common.Select;
import org.fao.fenix.server.tools.spring.SpringContext;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.Collection;
import java.util.Map;

@Path("msd")
public class MSD implements org.fao.fenix.msd.services.spi.MSD {
    @Context HttpServletRequest request;

    @Override
    public Collection<Map<String,Object>> select(Select queryInfo) throws Exception {
        return SpringContext.getBean(MSDDao.class).select(queryInfo);
    }
}
