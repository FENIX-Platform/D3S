package org.fao.fenix.msd.dto.dm;

import java.util.Collection;
import java.util.LinkedList;


public class DMMeta extends MMField {
    public static final String DEFAULT_FORMAT = "native";

    public DMMeta() {
        super();
        setAvailableDatasetsUID(new LinkedList<String>());
    }

    public String getUid() {
        return (String)get("metadataUID");
    }

    public void setUid(String uid) {
        put("metadataUID", uid);
    }

    public Collection<String> getAvailableDatasetsUID() {
        return (Collection<String>) get("availableDatasetsUID");
    }

    public void setAvailableDatasetsUID(Collection<String> availableDatasets) {
        put("availableDatasetsUID", availableDatasets);
    }


    //Utils
    public void addDataset(String uid) {
        getAvailableDatasetsUID().add(uid);
    }
}
