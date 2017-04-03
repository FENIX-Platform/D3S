package org.fao.fenix.export.d3s.legacy.md.data.dto;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Map;

public class MDSDOProperty {

    private String titleBean;
    private String titleToVisualize;
    private String description;
    private String type;
    private String order;
    private String required;
    private String reference;
    private String patternProperties;
    private ArrayList<Map.Entry<String,JsonNode>> properties;
    private Object value;
    private JsonNode items;


    public MDSDOProperty () {}

    public String getTitleBean() {
        return titleBean;
    }

    public void setTitleBean(String titleBean) {
        this.titleBean = titleBean;
    }

    public String getTitleToVisualize() {
        return titleToVisualize;
    }

    public void setTitleToVisualize(String titleToVisualize) {
        this.titleToVisualize = titleToVisualize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Object getProperties() {
        return properties;
    }

    public void setProperties( ArrayList<Map.Entry<String,JsonNode>> properties) {
        this.properties = properties;
    }

    public String getPatternProperties() {
        return patternProperties;
    }

    public void setPatternProperties(String patternProperties) {
        this.patternProperties = patternProperties;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public JsonNode getItems() {
        return items;
    }

    public void setItems(JsonNode items) {
        this.items = items;
    }
}
