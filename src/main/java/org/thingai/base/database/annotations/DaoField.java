package org.thingai.base.database.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DaoField {
    String name() default "";

    boolean primaryKey() default false;

    boolean nullable() default true;

    boolean autoIncrement() default false;

    String defaultValue() default "";

    String foreignKey() default "";

    String foreignKeyReference() default "";

    String foreignKeyOnDelete() default "";

    String foreignKeyOnUpdate() default "";
}