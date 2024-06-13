package com.lzh.game.framework.resource.data;

import com.lzh.game.framework.resource.Index;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GetterBuild {

    private static class FieldGetter implements Getter {

        private Field field;

        protected FieldGetter(Field field) {
            ReflectionUtils.makeAccessible(field);
            this.field = field;
        }

        @Override
        public Serializable get(Object bean) {
            return (Serializable) ReflectionUtils.getField(field, bean);
        }

        @Override
        public Class<?> getType() {
            return field.getType();
        }

        @Override
        public String name() {
            return field.getName();
        }
    }

    private static class MethodGetter implements Getter {

        private Method method;

        protected MethodGetter(Method method) {
            ReflectionUtils.makeAccessible(method);
            this.method = method;
        }

        @Override
        public Serializable get(Object bean) {
            return (Serializable) ReflectionUtils.invokeMethod(method, bean);
        }

        @Override
        public Class<?> getType() {
            return method.getReturnType();
        }

        @Override
        public String name() {
            return method.getName();
        }
    }

    private static class FieldIndexGetter<C> extends FieldGetter implements IndexGetter {

        private boolean unique;
        private String indexName;

        protected FieldIndexGetter(Field field) {
            super(field);
            Index index = field.getAnnotation(Index.class);
            this.unique = index.unique();
            this.indexName = index.value();
        }

        @Override
        public boolean unique() {
            return unique;
        }

        @Override
        public String name() {
            return indexName;
        }
    }

    private static class MethodIndexGetter extends MethodGetter implements IndexGetter {

        private boolean unique;
        private String indexName;

        protected MethodIndexGetter(Method method) {
            super(method);
            Index index = method.getAnnotation(Index.class);
            this.unique = index.unique();
            this.indexName = index.value();
        }

        @Override
        public boolean unique() {
            return unique;
        }

        @Override
        public String name() {
            return indexName;
        }
    }

    private static class IdGetter extends FieldGetter implements IndexGetter {

        private String name;

        protected IdGetter(Field field) {
            super(field);
        }

        @Override
        public boolean unique() {
            return true;
        }

        protected void setName(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }

    public static FieldIndexGetter createFieldIndex(Field field) {
        return new FieldIndexGetter<>(field);
    }

    public static MethodIndexGetter createMethodIndex(Method method) {
        return new MethodIndexGetter(method);
    }

    public static IdGetter createKeyIndex(Field field, String name) {
        IdGetter getter = new IdGetter(field);
        getter.setName(name);
        return getter;
    }

    public static boolean isIdGetter(Getter getter) {
        return getter instanceof IdGetter;
    }
}
