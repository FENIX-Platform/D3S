package org.fao.fenix.client;

import java.util.*;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public abstract class D3SClient {


    //INIT
    private String basePath = "";
    private ResteasyClient clientRest;

    public void initRest(String basePath) {
        if (!basePath.endsWith("/"))
            basePath += '/';
        this.basePath = basePath;
        clientRest = new ResteasyClientBuilder().build();
    }


    //UTILS
    protected <T> T getProxy(Class<T> interfaceClassObj) throws ClassNotFoundException {
        if (clientRest!=null) { //REST PROXY
            String path = this.getClass().getAnnotation(Path.class).value();
            return clientRest.target(basePath + (path.charAt(0)=='/' ? path.substring(1) : path)).proxy(interfaceClassObj);
        } else { //JMX PROXY
            return null;
        }
    }


}
