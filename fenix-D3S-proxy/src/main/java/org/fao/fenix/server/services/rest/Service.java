package org.fao.fenix.server.services.rest;

import java.util.*;

import javax.ws.rs.Path;

import org.fao.fenix.client.D3SClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public abstract class Service extends D3SClient {


    //Client init sequence
    private static String basePath = "";

    public static void init(Properties initProperties) {
        basePath = initProperties.getProperty("D3S.baseURL","/");
    }
    {
        if (basePath!=null)
            initRest(basePath);
    }

}
