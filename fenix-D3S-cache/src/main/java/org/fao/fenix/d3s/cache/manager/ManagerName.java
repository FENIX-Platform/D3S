package org.fao.fenix.d3s.cache.manager;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ManagerName {
    String value() default "";
}
