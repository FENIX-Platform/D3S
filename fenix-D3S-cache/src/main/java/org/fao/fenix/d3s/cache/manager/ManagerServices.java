package org.fao.fenix.d3s.cache.manager;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("cache")
@Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON)
public class ManagerServices {
    @Inject private CacheManagerFactory factory;

    @GET
    @Path("clean")
    public Integer cleanCache(@QueryParam("type") @DefaultValue("dataset") CacheResourceType resourceType, @QueryParam("manager") @DefaultValue("dataset") String managerName, @QueryParam("storage") @DefaultValue("h2") String storageName) throws Exception {
        CacheManager manager = factory.getInstance(resourceType,managerName,storageName);
        return manager!=null ? manager.clean() : null;
    }
}
