package org.fao.fenix.d3s.server.services.rest;


import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/v2")
public class ServiceRegistry extends ResourceConfig {

    public ServiceRegistry() {
        packages(true, "org.fao.fenix").
        register(JacksonFeature.class);
    }
}
