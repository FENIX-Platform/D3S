package org.fao.fenix.d3s.client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

public class D3SClient {


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
    public String getBasePath() { return basePath; }

    public <T> T getProxy(Class<T> interfaceClassObj, String ... pathPrefix) throws Exception {
        if (clientRest!=null) { //REST PROXY
            return clientRest.target(basePath + createServiceBasePath(pathPrefix)).proxy(interfaceClassObj);
        } else { //JMX PROXY
            Registry registry = LocateRegistry.getRegistry("host",8000);

            return null;
        }
    }


    private String createServiceBasePath(String[] pathPrefix) {
        int trimStart, trimEnd;
        StringBuilder path = new StringBuilder();
        if (pathPrefix!=null)
            for (String p : pathPrefix) {
                trimStart = p.startsWith("/") ? 1 : 0;
                trimEnd = p.endsWith("/") ? p.length()-1 : p.length();
                path.append(p.substring(trimStart,trimEnd)).append('/');
            }
        Path pathAnnotation = this.getClass().getAnnotation(Path.class);
        if (pathAnnotation!=null) {
            String p = pathAnnotation.value();
            trimStart = p.startsWith("/") ? 1 : 0;
            trimEnd = p.endsWith("/") ? p.length()-1 : p.length();
            path.append(p.substring(trimStart,trimEnd)).append('/');
        }
        return path.toString();
    }


}
