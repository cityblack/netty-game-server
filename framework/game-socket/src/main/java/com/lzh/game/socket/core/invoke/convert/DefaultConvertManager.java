package com.lzh.game.socket.core.invoke.convert;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.invoke.RequestConvert;
import com.lzh.game.socket.core.invoke.RequestConvertManager;
import com.lzh.game.socket.core.message.NetMessage;
import com.lzh.game.socket.core.session.Session;

import java.util.*;

/**
 * UnSafe
 */
public class DefaultConvertManager implements RequestConvertManager {

    private final Map<Class<?>, RequestConvert<?>> convertContain = new HashMap<>();

    {
        registerConvert(Session.class, Request::getSession);
        registerConvert(Request.class, (r) -> r);
        registerConvert(NetMessage.class, r -> r.getData());
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
