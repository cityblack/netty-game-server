package com.lzh.game.framework.resource.resource;

import com.lzh.game.framework.resource.Id;
import com.lzh.game.framework.resource.Resource;
import com.lzh.game.framework.resource.storage.StorageInstance;
import com.lzh.game.framework.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Resource
@Getter
@Setter
public class ConfigValueResource<T> implements StorageInstance<T> {

    private static Map<String, Class<?>> BASE_TYPE_MAPPING;

    @Id
    private String key;

    private String value;

    private String type;

    private String clz;

    private volatile T target;

    enum Type {
        ARRAY,
        SET,
        LIST,
        OBJECT
    }

    @Override
    public T getValue() {
        if (Objects.nonNull(target)) {
            return target;
        }
        synchronized (this) {
            if (Objects.isNull(target)) {
                this.target = parse();
            }
        }
        return target;
    }

    private T parse() {
        try {
            Class<?> targetClz = parseClass(clz);
            // remove {}
            String content = value.substring(1, value.length() - 1);
            Type type = Type.valueOf(this.type.toUpperCase(Locale.ROOT));
            switch (type) {
                case ARRAY: {
                    return (T) JsonUtils.toArray(content, targetClz);
                }
                case LIST: {
                    return (T) JsonUtils.toCollection(content, ArrayList.class, targetClz);
                }
                case SET: {
                    return (T) JsonUtils.toCollection(content, HashSet.class, targetClz);
                }
                case OBJECT: {
                    return (T) JsonUtils.toBean(content, targetClz);
                }
                default: {
                    throw new IllegalArgumentException("Not defined type:" + this.type);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private Class<?> parseClass(String type) throws ClassNotFoundException {
        Class<?> base = BASE_TYPE_MAPPING.get(type);
        if (Objects.nonNull(base)) {
            return base;
        }
        return Class.forName(type);
    }

    static {
        BASE_TYPE_MAPPING = new HashMap<>();
        BASE_TYPE_MAPPING.put("Integer", Integer.class);
        BASE_TYPE_MAPPING.put("Long", Long.class);
        BASE_TYPE_MAPPING.put("Float", Float.class);
        BASE_TYPE_MAPPING.put("Boolean", Boolean.class);
        BASE_TYPE_MAPPING.put("Double", Double.class);
        BASE_TYPE_MAPPING.put("String", String.class);
        BASE_TYPE_MAPPING.put("Byte", Byte.class);
        BASE_TYPE_MAPPING.put("Short", Short.class);
    }
}
