package org.fao.fenix.export.d3s.legacy.md.layout.utils;


public class IndexModel {

    private String title;
    private Object object;

    public IndexModel(String title, Object object) {
        this.title = title;
        this.object = object;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
