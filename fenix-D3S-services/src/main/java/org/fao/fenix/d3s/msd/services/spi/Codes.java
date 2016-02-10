package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.find.dto.filter.CodesFilter;
import org.fao.fenix.commons.msd.dto.data.Direction;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

public interface Codes {

    @GET
    @Path("/hierarchy/{uid}/{version}/{code}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public org.fao.fenix.commons.msd.dto.full.Code getCodeHierarchy(@PathParam("uid") String uid,@PathParam("version") String version,@PathParam("code") String code, @QueryParam("depth") Integer depth, @QueryParam("direction") Direction direction) throws Exception;
    @GET
    @Path("/hierarchy/{uid}/{code}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public org.fao.fenix.commons.msd.dto.full.Code getCodeHierarchy(@PathParam("uid") String uid,@PathParam("code") String code, @QueryParam("depth") Integer depth, @QueryParam("direction") Direction direction) throws Exception;

    @POST
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON)
    public Collection<Code> getCodes(CodesFilter filter, @QueryParam("tree") @DefaultValue("false") boolean tree) throws Exception;

}
