package com.lzh.game.socket.core.invoke.convert;

import com.lzh.game.socket.RemotingCommand;
import com.lzh.game.socket.core.invoke.RequestConvert;
import com.lzh.game.socket.core.invoke.RequestConvertManager;
import com.lzh.game.socket.core.session.Session;

import java.util.*;

/**
 * UnSafe
 */
public class DefaultConvertManager implements RequestConvertManager {

    private final Map<Class<?>, RequestConvert<?>> convertContain = new HashMap<>();

    {
        registerConvert(Session.class, RemotingCommand::getSession);
        registerConvert(Request.class, (r) -> r);
        registerConvert(Integer.class, new ProtoBufferConvert<>(Integer.class));
        registerConvert(String.class, new ProtoBufferConvert<>(String.class));
        registerConvert(Float.class, new ProtoBufferConvert<>(Float.class));
        registerConvert(Double.class, new ProtoBufferConvert<>(Double.class));
        registerConvert(Long.class, new ProtoBufferConvert<>(Long.class));
        registerConvert(int.class, new ProtoBufferConvert<>(Integer.class));
        registerConvert(double.class, new ProtoBufferConvert<>(Double.class));
        registerConvert(float.class, new ProtoBufferConvert<>(Float.class));
        registerConvert(long.class, new ProtoBufferConvert<>(Long.class));
    }

    @Override
    public void registerConvert(Class<?> target, RequestConvert<?> convert) {
        convertContain.put(target, convert);
    }

    @Override
    public RequestConvert<?> getConvert(Class<?> clazz) {
        return convertContain.get(clazz);
    }

    @Override
    public boolean hasConvert(Class<?> target) {
        return convertContain.containsKey(target);
    }

    @Override
    public RequestConvert<?> getOrCreateDefaultConvert(Class<?> clazz) {
        RequestConvert<?> convert = getConvert(clazz);
        if (Objects.isNull(convert)) {
            synchronized (this.convertContain) {
                if (!this.convertContain.containsKey(clazz)) {
                    registerConvert(clazz, new ProtoBufferConvert<>(clazz));
                    return getConvert(clazz);
                }
            }
        }
        return convert;
    }
}
