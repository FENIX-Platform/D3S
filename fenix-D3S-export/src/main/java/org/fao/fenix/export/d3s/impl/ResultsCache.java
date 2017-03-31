package org.fao.fenix.export.d3s.impl;

import org.fao.fenix.export.core.controller.GeneralController;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@ApplicationScoped
public class ResultsCache {

    Map<String, GeneralController> cache = new HashMap();
    Map<String, Long> times = new HashMap<>();

    public ResultsCache() { }

    public GeneralController put(String key, GeneralController value) {
        cleanCache();
        times.put(key, System.currentTimeMillis());
        return cache.put(key, value);
    }

    public GeneralController remove(Object key) {
        times.remove(key);
        return cache.remove(key);
    }


    //Lazy timer
    private static final long timeoutMillis = 30000;
    private synchronized void cleanCache() {
        Collection<String> toRemove = new LinkedList<>();
        for (Map.Entry<String,Long> timesEntry : times.entrySet())
            if (System.currentTimeMillis()-timesEntry.getValue()>timeoutMillis)
                toRemove.add(timesEntry.getKey());
        for (String toRemoveId : toRemove)
            remove(toRemoveId);
    }
}
