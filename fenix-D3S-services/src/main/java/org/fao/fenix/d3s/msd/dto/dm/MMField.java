package org.fao.fenix.d3s.msd.dto.dm;

import java.util.HashMap;
import java.util.Map;

public class MMField extends HashMap<String,Object> {
    //Mapping
    public Map<String,Object> getMapping() {
        Map<String,Object> mapping = (Map<String,Object>)get("_mapping");
        if (mapping==null)
            put("_mapping", mapping = new HashMap<String, Object>());
        return mapping;
    }
    public String getFieldName() {
        return (String)getMapping().get("fieldName");
    }
    public String getFieldType() {
        return (String)getMapping().get("fieldName");
    }
    public String getFieldSourceType() {
        return (String)getMapping().get("fieldName");
    }
    public String getEncoder() {
        return (String)getMapping().get("encoder");
    }
    public String getDecoder() {
        return (String)getMapping().get("decoder");
    }
    //Description
    public Map<String,Object> getDescription() {
        Map<String,Object> description = (Map<String,Object>)get("_description");
        if (description==null)
            put("_description", description = new HashMap<String, Object>());
        return description;
    }
    public String getTitle(String language) {
        Map<String,Object> description = getDescription();
        return description.containsKey("title") ? (String)((Map<String,Object>)description.get("title")).get(language) : null;
    }
    public String getSummary(String language) {
        Map<String,Object> description = getDescription();
        return description.containsKey("summary") ? (String)((Map<String,Object>)description.get("summary")).get(language) : null;
    }
    public String getAbstract(String language) {
        Map<String,Object> description = getDescription();
        return description.containsKey("abstract") ? (String)((Map<String,Object>)description.get("abstract")).get(language) : null;
    }
    public String getSupplemental(String language) {
        Map<String,Object> description = getDescription();
        return description.containsKey("supplemental") ? (String)((Map<String,Object>)description.get("supplemental")).get(language) : null;
    }

}
