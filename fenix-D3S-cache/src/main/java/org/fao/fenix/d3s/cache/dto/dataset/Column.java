package org.fao.fenix.d3s.cache.dto.dataset;


import org.fao.fenix.commons.msd.dto.full.DSDColumn;


public class Column {

    private String name;
    private Type type;
    private boolean key;
    private Object noDataValue;


    public Column(DSDColumn columnMetadata) {
        if ((name = columnMetadata.getSubject()) == null)
            name = columnMetadata.getId();

        switch (columnMetadata.getDataType()) {
            case bool: type = Type.bool; break;
            case code:
            case customCode:
            case enumeration:
            case text: type = Type.string; break;
            case date:
            case month:
            case year:
            case time: type = Type.integer; break;
            case number:
            case percentage: type = Type.real; break;
            case label: type = Type.object; break;
        }

        key = columnMetadata.getKey()!=null && columnMetadata.getKey();

        //TODO noDataValue = columnMetadata.getNoDataValue();
    }


    public Column(String name, Type type, boolean key) {
        this.name = name;
        this.type = type;
        this.key = key;
    }




    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isKey() {
        return key;
    }

    public Object getNoDataValue() {
        return noDataValue;
    }
}
