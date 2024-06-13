package com.lzh.game.framework.resource;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Static {
    /**
     * The value can't be null when the annotation use in describing {@link ConfigValue} field
     * @return
     */
    String value() default "";
}
