package org.fao.fenix.d3s.cache.storage;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface StorageName {
    String value() default "";
}
