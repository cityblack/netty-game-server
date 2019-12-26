package com.lzh.game.resource;

import com.lzh.game.resource.uitl.IndexComparator;

import java.lang.annotation.*;
import java.util.Comparator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Id {

    Class<? extends Comparator> comparator() default IndexComparator.class;

}
