package org.fao.fenix.d3s.msd.services.spi;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

public interface Enumerations {

    @GET
    @Path("/{enumeration}")
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Map<String, Map<String,String>> getEnumeration(@PathParam("enumeration") String enumName) throws Exception;
    @GET
    @Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
    public Object getAvailableEnumerations(@QueryParam("full") @DefaultValue("false") boolean full) throws Exception;
}
