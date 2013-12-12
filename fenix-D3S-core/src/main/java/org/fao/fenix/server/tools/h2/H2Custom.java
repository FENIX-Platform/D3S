package org.fao.fenix.server.tools.h2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface H2Custom {
    String value();
}
