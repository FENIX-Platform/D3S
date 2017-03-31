package org.fao.fenix.export.d3s.impl;

import org.fao.fenix.export.core.controller.GeneralController;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class ResultsCache {

    Map<String, GeneralController> cache = new HashMap();

    public ResultsCache() { }

    public GeneralController put(String key, GeneralController value) {
        return cache.put(key, value);
        //TODO implement timeout with TimerTask
    }

    public GeneralController get(Object key) {
        return cache.get(key);
    }


    public GeneralController remove(Object key) {
        return cache.remove(key);
    }
}
