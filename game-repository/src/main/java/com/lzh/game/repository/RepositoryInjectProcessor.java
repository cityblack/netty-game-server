package com.lzh.game.repository;

import com.lzh.game.repository.config.GameRepositoryConfig;
import com.lzh.game.repository.db.Persist;
import com.lzh.game.repository.db.PersistEntity;
import com.lzh.game.repository.db.PersistRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cache.CacheManager;
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

@Component
public class RepositoryInjectProcessor implements BeanPostProcessor, Ordered, ApplicationContextAware {

    private ApplicationContext applicationContext;

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
        if (!PersistEntity.class.isAssignableFrom(dataType)) {
            throwInjectTypeError(bean, field);
        }
        DataRepository<?, ?> repository = createRepository(dataType);

        injectValue(field, bean, repository);
    }

    private void throwInjectTypeError(Object bean, Field field) {
        throw new IllegalArgumentException(MessageFormat.format("[{0} {1}] type is illegal."
                , bean.getClass().getName(), field.getName()));
    }

    private DataRepository<?, ?> createRepository(Class<?> dataClass) {
        CacheManager cacheManager = applicationContext.getBean(CacheManager.class);
        Persist persist = applicationContext.getBean(Persist.class);
        PersistRepository repository = applicationContext.getBean(PersistRepository.class);
        GameRepositoryConfig config = applicationContext.getBean(GameRepositoryConfig.class);

        return new DataRepositoryImpl(cacheManager, persist, dataClass, repository, config.isClearRedisAfterClose());
    }

    private void injectValue(Field field, Object bean, DataRepository<?, ?> data) {
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, data);
    }
}
