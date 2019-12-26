package com.lzh.game.common.bean;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class BeanUtil extends BeanUtils {

    private static final BeanToMapConfig DEFAULT_BEAN_CONFIG = (field) -> Objects.isNull(field.getAnnotation(JsonIgnore.class));

    public static Map<String, Object> beanToMap(Object bean, BeanToMapConfig config) {
        Map<String, Object> convent = new HashMap<>();
        foreachBeanWithConfig(bean, config, (k,v) -> convent.put(k, v));
        return convent;
    }

    public static Map<String, Object> beanToMap(Object bean) {
        return beanToMap(bean, DEFAULT_BEAN_CONFIG);
    }

    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        /*T entity = instantiateClass(clazz);
        map.forEach((k,v) -> {
           Field field = ReflectionUtils.findField(clazz, k);
           if (Objects.nonNull(field)) {
               ReflectionUtils.makeAccessible(field);
               ReflectionUtils.setField(field, entity, v);
           }
        });*/

        ;
        return TypeUtils.castToJavaBean(map, clazz, ParserConfig.getGlobalInstance());
    }

    public static <F,D>Map<F, D> beanToMap(Object bean, BeanSerializer<F> fieldSerializer, BeanSerializer<D> valueSerializer, BeanToMapConfig config) {

        Map<F, D> map = new LinkedHashMap<>();
        foreachBeanWithConfig(bean, config, (k,v) -> map.put(fieldSerializer.serialize(k), valueSerializer.serialize(v)));
        return map;
    }

    private static void foreachBeanWithConfig(Object bean, BeanToMapConfig config, BiConsumer<String, Object> nameValue) {

        ReflectionUtils.doWithFields(bean.getClass(), e -> {
            ReflectionUtils.makeAccessible(e);
            Object value = ReflectionUtils.getField(e, bean);
            if (Objects.isNull(value) && !config.ignoreNull()) {
                return;
            }
            nameValue.accept(e.getName(), value);
        }, method -> config.canTransfer(method));
    }
}
