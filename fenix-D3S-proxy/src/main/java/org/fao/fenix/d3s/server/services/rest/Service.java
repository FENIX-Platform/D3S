package org.fao.fenix.d3s.server.services.rest;

import java.util.*;

import org.fao.fenix.d3s.client.D3SClient;

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
