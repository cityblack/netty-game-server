package com.lzh.game.framework.resource;

import com.lzh.game.framework.resource.data.meta.NoneComparator;

import java.lang.annotation.*;
import java.util.Comparator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {

    String name() default "";

    Class<? extends Comparator<?>> comparator() default NoneComparator.class;
}
