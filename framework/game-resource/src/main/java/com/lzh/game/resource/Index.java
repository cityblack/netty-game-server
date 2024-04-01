package com.lzh.game.resource;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Index {

    String value();

    boolean unique() default false;
}
