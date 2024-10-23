package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.Rookie;
import com.lzh.game.framework.rookie.serializer.ClassInfo;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import com.lzh.game.framework.rookie.utils.Constant;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * {@link Field#get(Object)}{@link Field#set(Object, Object)}
 * Use javassist?
 * @author zehong.l
 * @since 2024-09-18 12:34
 **/
public class ObjectSerializer<T> extends ElementSerializer<T> {

    private FieldInfo[] fields;

    @Override
    public void initialize(Class<T> clz, Rookie rookie) {
        super.initialize(clz, rookie);
        if (clz.isInterface()) {
            throw new IllegalArgumentException("Not support interface");
        }
        List<FieldInfo> fields = new ArrayList<>();
        for (Field field : getAllFields(clz)) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            if (Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            if (field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);
            var classInfo = rookie.getClassInfo(field.getType());
            fields.add(new FieldInfo(classInfo, field));
        }
        this.fields = fields.toArray(FieldInfo[]::new);
    }

    private List<Field> getAllFields(Class<T> o) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> process = o; process != Object.class; process = process.getSuperclass()) {
            Collections.addAll(fields, process.getDeclaredFields());
        }
        return fields;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T readObject(ByteBuf in, Class<T> clz) {
        var bean = newBean(clz);
        for (FieldInfo field : fields) {
            var info = field.info;
            try {
                if (info.isFinalType()) {
                    var value = info.getSerializer().readObject(in, info.getClz());
                    field.field.set(bean, value);
                } else {
                    var writeInfo = readClassInfo(in);
                    // abstract type. use class from bytes
                    // collect type. use class from field
                    var target = writeInfo.getId() != info.getId() && writeInfo.isCollection()
                            ? info : writeInfo;
                    var value = target.getSerializer().readObject(in, target.getClz());
                    field.field.set(bean, value);
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Set object value error", e);
            }
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void writeObject(ByteBuf out, T o) {
        for (FieldInfo field : fields) {
            try {
                var value = field.field.get(o);

                var info = field.info;
                // Base type. value is not null.
                if (info.isFinalType()) {
                    info.getSerializer().writeObject(out, value);
                } else {
                    // Support abstract. write impl class to bytes.
                    writeElement(out, value);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error get object ", e);
            }
        }
    }

    static class FieldInfo {
        ClassInfo info;

        Field field;

        public FieldInfo(ClassInfo info, Field field) {
            this.info = info;
            this.field = field;
        }
    }
}
