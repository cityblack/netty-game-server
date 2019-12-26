package com.lzh.game.common.bean;

import java.lang.reflect.Field;

public interface BeanToMapConfig {

    boolean canTransfer(Field field);

    default boolean ignoreNull() {
        return false;
    }
}
