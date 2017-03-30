package org.fao.fenix.export.d3s.impl;

import org.fao.fenix.export.core.controller.GeneralController;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;

@ApplicationScoped
public class ResultsCache extends HashMap<String, GeneralController> {

    @Override
    public GeneralController put(String key, GeneralController value) {
        return super.put(key, value);
        //TODO implement timeout with TimerTask
    }

}
