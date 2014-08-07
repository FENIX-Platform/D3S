package org.fao.fenix.d3s.wds;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.util.HashMap;
import java.util.Map;

public abstract class WDSDao<T> {
    //Init
    public abstract boolean init();
    public abstract void init(Map<String,String> properties) throws Exception;

    //Load and store specific DAO operations
    public abstract T loadData(MeIdentification resource) throws Exception;
    public abstract void storeData(MeIdentification resource, T data, boolean overwrite) throws Exception;





    //Utils

}
