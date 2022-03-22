package com.lzh.game.resource.inject;

import com.lzh.game.resource.*;
import com.lzh.game.resource.data.ResourceManageHandler;
import javassist.*;
import javassist.util.proxy.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;

/**
 * Write @static value
 */
@Component
public class StaticInjectProcessor implements BeanPostProcessor, Ordered {

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

    @Autowired
    private ResourceManageHandler resourceManageHandler;

    @Autowired
    private ConversionService conversionService;

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

        StorageManage manage = getStorageManage();

        checkStorageAndThrow(dataType);

        Storage<?, ?> storage = manage.getStorage(dataType);
        injectValue(field, bean, storage);
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
        StorageManage manage = getStorageManage();
        Storage storage = manage.getStorage(type);

        // @Resource type has unique @Id field
        ReflectionUtils.doWithFields(type, f -> {
            Class<?> idType = f.getType();
            Object key = conversionService.convert(sign, idType);
            // Find instance by key
            StorageInstance instance = createProxyInstance(type, storage, key);
            injectValue(field, bean, instance);
        }, f -> f.isAnnotationPresent(Id.class));
    }

    /**
     * Use Javassist produce proxy object
     *
     * @param clz
     * @return
     */
    private StorageInstance createProxyInstance(Class<?> clz, Storage storage, Object key) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(clz);
//        StorageInstance instance = (StorageInstance) BeanUtils.instantiateClass(proxyFactory.createClass());
        return null;
        /*ClassPool cp = ClassPool.getDefault();
        // clz.getName()
        CtClass ctClass = cp.makeClass();
        cp.insertClassPath(new ClassClassPath(StorageInstance.class));
        try {
            String storageFieldName = "storage";
            String keyFieldName = "key";

            CtClass[] faces = new CtClass[]{cp.getCtClass(StorageInstance.class.getName())};
            ctClass.setInterfaces(faces);


            CtClass storageType = cp.getCtClass(Storage.class.getName());
            CtField storageField = new CtField(storageType, storageFieldName, ctClass);
            ctClass.addField(storageField);

            CtClass keyClass = cp.getCtClass(key.getClass().getName());
            CtField keyField = new CtField(keyClass, keyFieldName, ctClass);
            ctClass.addField(keyField);

            CtConstructor ctConstructor = new CtConstructor(new CtClass[]{storageType, keyClass}, ctClass);
            String constructor = String.format("{this.%s=%s;this.%s=%s}"
                    , storageFieldName, storageFieldName, keyFieldName, keyFieldName);
            ctConstructor.setBody(constructor);
            ctClass.addConstructor(ctConstructor);

            CtMethod ctMethod = new CtMethod(cp.getCtClass("java.lang.Object"), "getValue", new CtClass[]{}, ctClass);
            String body = "{Object value = storage.getOrThrow(key);return ((StorageInstance)value).getValue();}";
            ctMethod.setBody(body);
            ctClass.addMethod(ctMethod);
            StorageInstance instance = (StorageInstance) BeanUtils.instantiateClass(ctClass.toClass()
                    .getConstructor(storageType.toClass(), keyClass.toClass()), storage, key);

            return instance;
        } catch (NotFoundException e) {
            //
            return null;
        } catch (CannotCompileException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }*/
    }

    private void throwInjectTypeError(Object bean, Field field, String type) {
        throw new IllegalArgumentException(MessageFormat.format("[{0} {1} {2}] type is illegal."
                , bean.getClass().getName(), field.getName(), type));
    }

    private void injectValue(Field field, Object bean, Object value) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, value);
    }

    protected StorageManage getStorageManage() {
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
            throw new IllegalArgumentException("Not exist [" + type.getName() + "] type mapping resource. Please check the resource weather registered.");
        }
    }
}
