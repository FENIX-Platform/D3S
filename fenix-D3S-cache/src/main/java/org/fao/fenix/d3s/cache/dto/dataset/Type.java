package org.fao.fenix.d3s.cache.dto.dataset;

import java.sql.Types;

public enum Type {
    string(Types.VARCHAR), array(Types.ARRAY), integer(Types.BIGINT), real(Types.DOUBLE), bool(Types.BOOLEAN), object(Types.OTHER);



    private final int sqlType;

    private Type(int sqlType) {
        this.sqlType = sqlType;
    }

    public int getSqlType() {
        return sqlType;
    }

    public static Type getType(int sqlType) {
        switch (sqlType) {
            case Types.VARCHAR: return string;
            case Types.ARRAY: return array;
            case Types.INTEGER: return integer;
            case Types.DOUBLE: return real;
            case Types.BOOLEAN: return bool;
            case Types.OTHER: return object;
            default: return null;
        }
    }


}
