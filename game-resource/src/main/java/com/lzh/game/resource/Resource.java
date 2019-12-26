package com.lzh.game.resource;

import com.lzh.game.resource.uitl.IndexComparator;

import java.lang.annotation.*;
import java.util.Comparator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {

    String name() default "";

    Class<? extends Comparator> comparator() default IndexComparator.class;
}
