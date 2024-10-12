package com.lzh.game.framework.repository.inject;

import com.lzh.game.framework.repository.DataRepository;
import com.lzh.game.framework.repository.DataRepositoryImpl;
import com.lzh.game.framework.repository.DataRepositoryManager;
import com.lzh.game.framework.repository.anno.Repository;
import com.lzh.game.framework.repository.cache.CacheDataRepositoryImpl;
import com.lzh.game.framework.repository.cache.CacheFactory;
import com.lzh.game.framework.repository.config.RepositoryConfig;
import com.lzh.game.framework.repository.element.BaseEntity;
import com.lzh.game.framework.repository.persist.PersistFactory;
import com.lzh.game.framework.repository.persist.PersistRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class RepositoryInjectProcessor implements DataRepositoryManager, DisposableBean
        , BeanPostProcessor, Ordered, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<Class<?>, DataRepository<?,?>> repositories = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            injectInstance(bean, field);
        }, field -> field.isAnnotationPresent(Repository.class));
        return bean;
    }

    private void injectInstance(Object bean, Field field) {
        Type type = field.getGenericType();

        if (!(type instanceof ParameterizedType)) {
            throwInjectTypeError(bean, field);
        }
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (types.length != 2) {
            throwInjectTypeError(bean, field);
        }
        Class<?> keyType = (Class<?>) types[0];
        if (!Serializable.class.isAssignableFrom(keyType)) {
            throwInjectTypeError(bean, field);
        }
        Class<?> dataType = (Class<?>) types[1];
        if (!BaseEntity.class.isAssignableFrom(dataType)) {
            throwInjectTypeError(bean, field);
        }
        DataRepository<?, ?> repository = findRepository(dataType);

        injectValue(field, bean, repository);
    }

    private void throwInjectTypeError(Object bean, Field field) {
        throw new IllegalArgumentException(MessageFormat.format("[{0} {1}] type is illegal."
                , bean.getClass().getName(), field.getName()));
    }

    @Override
    public DataRepository<?, ?> findRepository(Class<?> dataClass) {
        var bean = this.repositories.get(dataClass);
        if (Objects.isNull(bean)) {
            synchronized (this) {
                bean = this.repositories.get(dataClass);
                if (Objects.isNull(bean)) {
                    bean = createRepository(dataClass);
                    this.repositories.put(dataClass, bean);
                }
            }
        }
        return bean;
    }

    @SuppressWarnings("all")
    private DataRepository<?, ?> createRepository(Class<?> dataClass) {

        var cacheFactory = applicationContext.getBean(CacheFactory.class);
        var persistFactory = applicationContext.getBean(PersistFactory.class);
        var repository = applicationContext.getBean(PersistRepository.class);
        var config = applicationContext.getBean(RepositoryConfig.class);
//        applicationContext.
        String name = dataClass.getName();
        var cache = cacheFactory.createCache(name, dataClass);
        var cacheRepository = new CacheDataRepositoryImpl(name, cache, dataClass);
        return new DataRepositoryImpl(cacheRepository, persistFactory.createPersist(dataClass), dataClass, repository, config.isClearCacheAfterClose());
    }

    private void injectValue(Field field, Object bean, DataRepository<?, ?> data) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, data);
    }


    @Override
    public void destroy() throws Exception {
        for (DataRepository<?, ?> value : this.repositories.values()) {
            value.shutdown();
        }
    }
}
