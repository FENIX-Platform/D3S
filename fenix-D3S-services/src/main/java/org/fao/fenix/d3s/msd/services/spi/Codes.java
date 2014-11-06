package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.find.dto.filter.CodesFilter;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

public interface Codes {

    @POST
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<Code> getCodes(CodesFilter filter) throws Exception;

}
