package com.lzh.game.resource.data;

import com.lzh.game.resource.Id;
import com.lzh.game.resource.Index;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Comparator;

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

    private static class FieldIndexGetter<C> extends FieldGetter implements IndexGetter<C> {

        private boolean unique;
        private String indexName;
        private Comparator<C> comparator;

        protected FieldIndexGetter(Field field) {
            super(field);
            Index index = field.getAnnotation(Index.class);
            this.unique = index.unique();
            this.indexName = index.value();
            this.comparator = newComparator(index.comparator());
        }

        @Override
        public Comparator<C> comparator() {
            return comparator;
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

    private static class MethodIndexGetter<C> extends MethodGetter implements IndexGetter<C> {

        private boolean unique;
        private String indexName;
        private Comparator<C> comparator;

        protected MethodIndexGetter(Method method) {
            super(method);
            Index index = method.getAnnotation(Index.class);
            this.unique = index.unique();
            this.indexName = index.value();
            this.comparator = newComparator(index.comparator());
        }

        @Override
        public Comparator<C> comparator() {
            return comparator;
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

    private static Comparator newComparator(Class<? extends Comparator> clazz) {

        return BeanUtils.instantiateClass(clazz);
    }

    private static class IdGetter<C> extends FieldGetter implements IndexGetter<C> {

        private String name;

        private Comparator<C> comparator;

        protected IdGetter(Field field) {
            super(field);
            Id id = field.getAnnotation(Id.class);
            this.comparator = newComparator(id.comparator());
        }

        @Override
        public Comparator<C> comparator() {
            return comparator;
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
