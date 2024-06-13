package com.lzh.game.framework.resource;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {

    String name() default "";

}
