package com.lzh.game.common.getter;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class FieldGetter implements Getter {

    private Field field;

    public FieldGetter(Field field) {
        ReflectionUtils.makeAccessible(field);
        this.field = field;
    }

    @Override
    public Object get(Object bean) {

        return ReflectionUtils.getField(field, bean);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public String name() {
        return field.getName();
    }

    public Field getField() {
        return field;
    }
}
