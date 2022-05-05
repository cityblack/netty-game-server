package com.lzh.game.socket.core.invoke;

import java.util.*;

public class DefaultConvertManager implements ConvertManager {

    private Map<Class<?>, ParamConvert<?>> convertContain = new HashMap<>();

    private Set<Class<?>> innerContain = new HashSet<>();

    @Override
    public boolean registerConvert(Class<?> target, ParamConvert<?> convert, boolean inner) {
        innerContain.add(target);
        return Objects.isNull(convertContain.putIfAbsent(target, convert));
    }

    @Override
    public ParamConvert<?> getConvert(Class<?> clazz) {
        return convertContain.get(clazz);
    }

    @Override
    public boolean isInnerConvert(Class<?> clazz) {
        return innerContain.contains(clazz);
    }

    @Override
    public Set<Class<?>> inner() {
        return new HashSet<>(this.innerContain);
    }

    @Override
    public boolean hasConvert(Class<?> target) {
        return convertContain.containsKey(target);
    }
}
