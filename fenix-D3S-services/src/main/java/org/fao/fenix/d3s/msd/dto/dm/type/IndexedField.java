package org.fao.fenix.d3s.msd.dto.dm.type;

import org.fao.fenix.d3s.msd.dto.dsd.type.DSDDataType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface IndexedField {

    public DSDDataType value();


}
