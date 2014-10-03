package org.fao.fenix.d3s.msd.services.spi;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.templates.identification.DSD;
import org.fao.fenix.commons.msd.dto.templates.identification.MeIdentification;
import org.fao.fenix.commons.utils.PATCH;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Codes {

    @POST
    @Path("/filter/rid/{rid}")
    public Collection<Code> getCodes(@PathParam("rid") String rid, @QueryParam("level") @DefaultValue("1") Integer level, @QueryParam("levels") Integer levels, Collection<String> codes) throws Exception;
    @POST
    @Path("/filter/uid/{uid}")
    public Collection<Code> getCodesByUID(@PathParam("uid") String uid, @QueryParam("level") @DefaultValue("1") Integer level, @QueryParam("levels") Integer levels, Collection<String> codes) throws Exception;
    @POST
    @Path("/filter/{uid}/{version}")
    public Collection<Code> getCodesByUID(@PathParam("uid") String uid, @PathParam("version") String version, @QueryParam("level") @DefaultValue("1") Integer level, @QueryParam("levels") Integer levels, Collection<String> codes) throws Exception;

}
