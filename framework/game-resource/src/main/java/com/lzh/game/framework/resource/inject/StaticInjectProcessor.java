package com.lzh.game.framework.resource.inject;

import com.lzh.game.framework.resource.Resource;
import com.lzh.game.framework.resource.Static;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.resource.storage.StorageInstance;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;

/**
 * Write @static value
 */
@Slf4j
@Component
public class StaticInjectProcessor implements BeanPostProcessor, Ordered {

    private final Map<Class<?>, Class<?>> INSTANCE_TYPE = new HashMap<>();

    enum InjectType {
        // Storage type
        STORAGE,
        // @Resource instance
        INSTANCE,

        ERROR;

        public static InjectType getType(Class<?> clz) {
            if (Storage.class.isAssignableFrom(clz)) {
                return InjectType.STORAGE;
            } else if (StorageInstance.class.isAssignableFrom(clz) && clz.isAnnotationPresent(Resource.class)) {
                return InjectType.INSTANCE;
            }
            return InjectType.ERROR;
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Autowired
    private StorageManageFactory storageManageFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        parseBean(bean);

        return bean;
    }

    private void parseBean(Object bean) {

        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            Static value = field.getAnnotation(Static.class);
            inject(bean, field, value);
        }, field -> field.isAnnotationPresent(Static.class));
    }

    private void inject(Object bean, Field field, Static s) {

        Class<?> fieldType = field.getType();
        InjectType type = InjectType.getType(fieldType);
        switch (type) {
            case STORAGE: {
                injectStorage(bean, field);
                break;
            }
            case INSTANCE: {
                injectInstance(bean, field, s);
                break;
            }
            default:
                throw new IllegalArgumentException("@Static can't auto write the " + fieldType.getSimpleName() + " to "
                        + bean.getClass().getSimpleName());
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

        StorageManager manage = getStorageManage();

        checkStorageAndThrow(dataType);

        injectValue(field, bean, manage.getStorage(dataType));
    }

    /**
     * # @Static("value")
     * example: # ConfigValue<String> -> ConfigValue extends StorageInstance
     *
     * @param bean
     * @param field
     * @param s
     */
    private void injectInstance(Object bean, Field field, Static s) {
        String sign = s.value();
        if (!StringUtils.hasText(sign)) {
            throw new IllegalArgumentException("@Static auto write instance error due to @Static value is empty.");
        }
        Class<?> type = field.getType();

        checkStorageAndThrow(type);
        StorageManager manage = getStorageManage();
        Storage storage = manage.getStorage(type);
        StorageInstance<?> instance = createProxyInstance(type, storage, sign);
        injectValue(field, bean, instance);
    }

    /**
     *
     * @return
     */
    private StorageInstance<?> createProxyInstance(Class<?> type, Storage storage, String key) {
        try {
            var bean = getInstanceType(type).newInstance();
            ((Proxy) bean).setHandler(new StorageInstanceBridge(storage, key));
            return (StorageInstance) bean;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> getInstanceType(Class<?> type) {
        var result = INSTANCE_TYPE.get(type);
        if (Objects.isNull(result)) {
            synchronized (INSTANCE_TYPE) {
                if (!INSTANCE_TYPE.containsKey(type)) {
                    ProxyFactory factory = new ProxyFactory();
                    factory.setInterfaces(new Class[]{StorageInstance.class});
                    factory.setSuperclass(type);
                    Class<?> clz = factory.createClass();
                    INSTANCE_TYPE.put(type, clz);
                }
                return INSTANCE_TYPE.get(type);
            }
        }
        return result;
    }

    private void throwInjectTypeError(Object bean, Field field, String type) {
        throw new IllegalArgumentException(MessageFormat.format("[{0} {1} {2}] type is illegal."
                , bean.getClass().getName(), field.getName(), type));
    }

    private void injectValue(Field field, Object bean, Object value) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, value);
    }

    protected StorageManager getStorageManage() {
        try {
            return storageManageFactory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void checkStorageAndThrow(Class<?> type) {
        if (!getStorageManage().containStorage(type)) {
            throw new IllegalArgumentException("Not exist [" + type.getName() + "] type mapping resource. Please check the resource weather registered.");
        }
        if (type.isAssignableFrom(StorageInstance.class)) {
            throw new IllegalArgumentException("Error defined [" + type.getName() + "]. Storage instance must extends StorageInstance interface.");
        }
    }

    private static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        for (Class<?> intf : clazz.getInterfaces()) {
            if (intf.getInterfaces().length > 0) {
                interfaces.addAll(getAllInterfaces(intf));
            }
            interfaces.add(intf);
        }
        if (clazz.getSuperclass() != null) {
            interfaces.addAll(getAllInterfaces(clazz.getSuperclass()));
        }

        if (clazz.isInterface()) {
            interfaces.add(clazz);
        }

        return interfaces;
    }
}
