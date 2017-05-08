package org.fao.fenix.export.d3s.impl;

import org.fao.fenix.export.core.controller.GeneralController;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

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
        cleanCache();
        times.remove(key);
        return cache.remove(key);
    }


    //Lazy timer
    private static final long timeoutMillis = 30000;
    private synchronized void cleanCache() {
        for (Iterator<Map.Entry<String,Long>> timesEntryIterator = times.entrySet().iterator(); timesEntryIterator.hasNext();) {
            Map.Entry<String,Long> timesEntry = timesEntryIterator.next();
            if (System.currentTimeMillis()-timesEntry.getValue()>timeoutMillis) {
                cache.remove(timesEntry.getKey());
                timesEntryIterator.remove();
            }
        }
    }
}
