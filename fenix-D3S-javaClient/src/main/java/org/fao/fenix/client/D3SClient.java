package org.fao.fenix.client;

import java.util.*;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public abstract class D3SClient {


    //INIT
    private String basePath = "";
    private ResteasyClient clientRest;

    public void init(Properties initProperties) {
        basePath = initProperties.getProperty("D3S.baseURL","/");
        if (!basePath.endsWith("/"))
            basePath += '/';

        clientRest = new ResteasyClientBuilder().build();
    }


    //UTILS
    protected String getBasePath() {
        return basePath;
    }

    protected <T> T getProxy(Class<T> interfaceClassObj) throws ClassNotFoundException {
        String path = this.getClass().getAnnotation(Path.class).value();
        return clientRest.target(basePath + (path.charAt(0)=='/' ? path.substring(1) : path)).proxy(interfaceClassObj);
    }


}
