package com.lzh.game.resource;

/**
 *
 * @param <T>
 */
public interface ConfigValue<T> {

    T getValue();

    Class<?> mappingClass();
}
