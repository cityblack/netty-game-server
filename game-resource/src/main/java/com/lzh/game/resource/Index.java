package com.lzh.game.resource;

import com.lzh.game.resource.uitl.IndexComparator;

import java.lang.annotation.*;
import java.util.Comparator;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Index {

    String value();

    boolean unique() default false;

    Class<? extends Comparator> comparator() default IndexComparator.class;
}
