package com.lzh.game.common.bean;


public interface BeanSerializer<T> {

    T serialize(Object value);

    Object deserialize(T data);
}
