package org.fao.fenix.d3s.cache.dto.dataset;

import java.sql.Types;

public enum Type {
    string(Types.VARCHAR), integer(Types.INTEGER), real(Types.DOUBLE), bool(Types.BOOLEAN), object(Types.BLOB);



    private final int sqlType;

    private Type(int sqlType) {
        this.sqlType = sqlType;
    }
}
