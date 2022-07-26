package com.lzh.game.resource.inject;

import com.lzh.game.resource.*;
import com.lzh.game.resource.data.ResourceManageHandle;
import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Write @static value
 */
@Slf4j
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
    private ResourceManageHandle resourceManageHandle;

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
            try {
                StorageInstance instance = createProxyInstance(type, storage, key);
                injectValue(field, bean, instance);
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }, f -> f.isAnnotationPresent(Id.class));
    }

    /**
     * Use Javassist produce proxy object
     * Object extends StorageInstance
     *
     * @param clz
     * @return
     */
    private StorageInstance createProxyInstance(Class<?> clz, Storage storage, Object key) throws NotFoundException, CannotCompileException, NoSuchMethodException {
        Class<?> primaryInterface = StorageInstance.class;
        String methodBody = "{ Object value = storage.getOrThrow(key);return ((com.lzh.game.resource.StorageInstance)value).getValue(); }";

        String superClassName = clz.getName();
        String newClassName = superClassName.replaceAll("(.+)\\.(\\w+)", "$1.GameResource$2");
        ClassPool classPool = ClassPool.getDefault();

        classPool.appendClassPath(new ClassClassPath(clz));

        CtClass superCt = classPool.getCtClass(superClassName);
        CtClass targetCt = classPool.makeClass(newClassName, superCt);
        targetCt.setModifiers(Modifier.setPublic(Modifier.FINAL));

        log.info("Generating {}" + newClassName);

        // storage and key field
        CtClass storageType = classPool.getCtClass(Storage.class.getName());
        CtField storageField = new CtField(storageType, "storage", targetCt);
        targetCt.addField(storageField);

        CtClass keyClass = classPool.getCtClass(key.getClass().getName());
        CtField keyField = new CtField(keyClass, "key", targetCt);
        targetCt.addField(keyField);

        // constructor
        CtConstructor constructor = new CtConstructor(new CtClass[]{storageType, keyClass}, targetCt);
        String constructorBody = "{ this.storage = $1; this.key = $2; }";
        constructor.setBody(constructorBody);
        targetCt.addConstructor(constructor);

        // Make a set of method signatures we inherit implementation for, so we don't generate delegates for these
        Set<String> superSigs = new HashSet<>();
        for (CtMethod method : superCt.getMethods()) {
            if ((method.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                superSigs.add(method.getName() + method.getSignature());
            }
        }

        Set<String> methods = new HashSet<>();
        for (Class<?> intf : getAllInterfaces(primaryInterface)) {
            CtClass intfCt = classPool.getCtClass(intf.getName());
            targetCt.addInterface(intfCt);
            for (CtMethod intfMethod : intfCt.getDeclaredMethods()) {
                final String signature = intfMethod.getName() + intfMethod.getSignature();

                // don't generate delegates for methods we override
                if (superSigs.contains(signature)) {
                    continue;
                }

                // Ignore already added methods that come from other interfaces
                if (methods.contains(signature)) {
                    continue;
                }

                // Track what methods we've added
                methods.add(signature);

                // Clone the method we want to inject into
                CtMethod method = CtNewMethod.copy(intfMethod, targetCt, null);
                // StorageInstance#getValue
                if (method.getName().equals("getValue")) {
                    String modifiedBody = methodBody;
                    method.setBody(modifiedBody);
                }

                targetCt.addMethod(method);
            }
        }
        Class<?> resultClz = targetCt.toClass();
        Constructor<?> resultClzConstructor = resultClz.getConstructor(Storage.class, key.getClass());
        return (StorageInstance) BeanUtils.instantiateClass(resultClzConstructor, storage, key);
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

    protected ResourceManageHandle getResourceManageHandler() {
        return resourceManageHandle;
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
