package com.lzh.game.resource.inject;

import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.resource.ConfigValue;
import com.lzh.game.resource.Static;
import com.lzh.game.resource.Storage;
import com.lzh.game.resource.data.ResourceManageHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class StaticInjectProcessor implements BeanPostProcessor, Ordered {

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private static class DefaultConfigValue<T> implements ConfigValue<T> {

        private String sign;
        private ResourceManageHandler resourceManageHandler;
        private Class<T> dataType;

        /**
         * Is collect or not
         */
        private boolean collect;

        @Override
        public T getValue() {

            ConfigValueResource data = resourceManageHandler.findById(ConfigValueResource.class, sign);

            if (collect) {
                return (T) JsonUtils.toCollection(data.getValue(), ArrayList.class, dataType);
            } else {
                return JsonUtils.toObj(data.getValue(), dataType);
            }

        }

        @Override
        public Class<?> mappingClass() {
            return ConfigValueResource.class;
        }

        private void init() {

            if (Collection.class.isAssignableFrom(dataType)) {
                collect = true;
            }
        }

        public DefaultConfigValue(String sign, ResourceManageHandler resourceManageHandler, Class<T> dataType) {
            this.sign = sign;
            this.resourceManageHandler = resourceManageHandler;
            this.dataType = dataType;
            this.init();
        }
    }

    @Autowired
    private StorageManageFactory storageManageFactory;

    @Autowired
    private ResourceManageHandler resourceManageHandler;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        parseBean(bean);

        return bean;
    }

    private void parseBean(Object bean) {

        ReflectionUtils.doWithFields(bean.getClass(), field -> {

            Static value = field.getAnnotation(Static.class);
            injectInstance(bean, field, value);

        }, field -> field.isAnnotationPresent(Static.class));
    }

    private void injectInstance(Object bean, Field field, Static s) {

        Class<?> fieldType = field.getType();

        if (fieldType.equals(Storage.class)) {

            injectStorage(bean, field);

        } else if (fieldType.equals(ConfigValue.class)) {

            injectConfigValue(bean, field, s);

        } else {

            throw new IllegalArgumentException("@Static can't auto write the " + fieldType.getSimpleName() + " to "
                    + bean.getClass().getSimpleName()
                    + " bean due to the inject just support Storage or ConfigValue type");
        }
    }

    private void injectStorage(Object bean, Field field) {

        Type type = field.getGenericType();

        if (!(type instanceof ParameterizedType)) {
            throwInjectTypeError(bean, field, "Storage");
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (types.length != 2) {
            throwInjectTypeError(bean, field, "Storage");
        }
        Class<?> dataType = (Class<?>) types[1];

        DefaultStorageManage manage = getStorageManage();
       /* if (!getResourceManageHandler().isAvailableKey(dataType, types[0])) {
            throw new IllegalArgumentException(MessageFormat.format("Storage type is illegal. The [{0} {1}] Key type is illegal"
                    , bean.getClass().getSimpleName()
                    , field.getName()));
        }*/
        checkStorageAndThrow(dataType);

        Storage storage = manage.getStorage(dataType);
        injectValue(field, bean, storage);
    }

    private void injectConfigValue(Object bean, Field field, Static s) {
        String sign = s.sign();
        if (!StringUtils.hasText(sign)) {
            throw new IllegalArgumentException("@Static auto write ConfigValue error due to @Static sign is empty.");
        }
        Type type = field.getGenericType();
        if (!(type instanceof ParameterizedType)) {
            throwInjectTypeError(bean, field, "ConfigValue");
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (types.length != 1) {
            throwInjectTypeError(bean, field, "ConfigValue");
        }

        Class<?> dataType = (Class<?>) types[0];
        checkStorageAndThrow(ConfigValueResource.class);

        ConfigValue value = new DefaultConfigValue(sign, getResourceManageHandler(), dataType);
        injectValue(field, bean, value);
    }

    private void throwInjectTypeError(Object bean, Field field, String type) {
        throw new IllegalArgumentException(MessageFormat.format("[{0} {1} {2}] type is illegal."
                , bean.getClass().getName(), field.getName(), type));
    }

    private void injectValue(Field field, Object bean, Object value) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, value);
    }

    protected DefaultStorageManage getStorageManage() {
        try {
            return storageManageFactory.getObject();
        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    protected ResourceManageHandler getResourceManageHandler() {
        return resourceManageHandler;
    }

    private void checkStorageAndThrow(Class<?> type) {
        if (!getStorageManage().containStorage(type)) {
            throw new IllegalArgumentException("Not exist [" +  type.getName() + "] type mapping resource. Please check the resource weather registered.");
        }
    }
}
