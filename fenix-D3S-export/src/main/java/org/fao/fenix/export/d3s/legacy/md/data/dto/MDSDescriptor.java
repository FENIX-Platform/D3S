package org.fao.fenix.export.d3s.legacy.md.data.dto;

public class MDSDescriptor {

    private String titleBean;
    private String titleToVisualize;
    private String description;
    private Object value;

    public MDSDescriptor(String titleBean, String titleToVisualize, String description, Object value) {
        this.titleBean = titleBean;
        this.value = value;
        this.description = description;
        this.titleToVisualize = titleToVisualize;
    }

    public MDSDescriptor(String titleBean, String titleToVisualize, String description){
        this.titleBean = titleBean;
        this.description = description;
        this.titleToVisualize = titleToVisualize;
    }


    public MDSDescriptor(){};

    public Object getValue() {
        return value;
    }

    public MDSDescriptor setValue(Object value) {
        this.value = value;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitleToVisualize() {
        return titleToVisualize;
    }

    public void setTitleToVisualize(String titleToVisualize) {
        this.titleToVisualize = titleToVisualize;
    }

    public String getTitleBean() {
        return titleBean;
    }

    public void setTitleBean(String titleBean) {
        this.titleBean = titleBean;
    }
}
