package org.fao.fenix.d3s.server.services.rest;


import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class ServiceRegistry extends ResourceConfig {

    public ServiceRegistry() {
        packages(true, "org.fao.fenix");
        register(JacksonFeature.class);
    }
}
