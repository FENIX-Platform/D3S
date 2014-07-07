package org.fao.fenix.d3s.msd.services.spi.canc;

import org.fao.fenix.commons.msd.dto.templates.canc.common.Select;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;

public interface MSD {

    @Path("select")
    @POST
    @Produces(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_JSON+"; charset=UTF-8")
    public Collection<Map<String,Object>> select(Select queryInfo) throws Exception;


}
