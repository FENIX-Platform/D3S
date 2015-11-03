package org.fao.fenix.d3s.cache.manager.listener;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Context {
    String[] value() default "";
}
