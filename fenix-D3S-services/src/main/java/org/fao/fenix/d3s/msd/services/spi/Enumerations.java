package org.fao.fenix.d3s.msd.services.spi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface Enumerations {

    @GET
    @Path("/{enumeration}")
    public Map<String, Map<String,String>> getEnumeration(@PathParam("enumeration") String enumName) throws Exception;
    @GET
    public Object getAvailableEnumerations(@QueryParam("full") @DefaultValue("false") boolean full) throws Exception;
}
