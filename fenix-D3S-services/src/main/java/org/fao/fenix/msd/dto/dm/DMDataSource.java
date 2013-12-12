package org.fao.fenix.msd.dto.dm;

import org.fao.fenix.msd.dto.dm.type.DMDataSourceType;

import java.util.HashMap;
import java.util.Map;

public class DMDataSource {

    private Map<String,String> title;
    private Map<String,String> reference;
    private DMDataSourceType type;

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Map<String, String> getReference() {
        return reference;
    }

    public void setReference(Map<String, String> reference) {
        this.reference = reference;
    }

    public DMDataSourceType getType() {
        return type;
    }

    public void setType(DMDataSourceType type) {
        this.type = type;
    }

    //Utils
    public void addTitle(String language, String label) {
        if (title==null)
            title = new HashMap<String, String>();
        title.put(language, label);
    }
    public void addReference (String language, String label) {
        if (reference==null)
            reference = new HashMap<String, String>();
        reference.put(language, label);
    }

}
