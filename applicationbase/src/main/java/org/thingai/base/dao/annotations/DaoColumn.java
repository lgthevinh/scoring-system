package org.thingai.base.dao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DaoColumn {
    String name() default "";
    boolean primaryKey() default false;
    boolean nullable() default true;
    boolean autoIncrement() default false;
    boolean unique() default false;
    String defaultValue() default "";
}