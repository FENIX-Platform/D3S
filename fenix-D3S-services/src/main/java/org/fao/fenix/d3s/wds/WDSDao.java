package org.fao.fenix.d3s.wds;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class WDSDao<T> {

    //Factory support
    private static Map<String, Class<? extends WDSDao>> classes = new HashMap<>();

    public final static void register(String name, Class<? extends WDSDao> classObject) {
        classes.put(name, classObject);
    }

    public final static Class<? extends WDSDao> getDaoClass(String daoName) {
        return classes.get(daoName);
    }

    //Load and store specific DAO operations
    public abstract T loadData(MeIdentification resource) throws Exception;
    public abstract void storeData(MeIdentification resource, T data) throws Exception;



    //Utils

}
