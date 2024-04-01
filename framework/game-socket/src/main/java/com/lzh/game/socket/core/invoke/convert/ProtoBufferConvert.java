package com.lzh.game.socket.core.invoke.convert;

import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.invoke.RequestConvert;

import java.util.Objects;

public class ProtoBufferConvert<T> implements RequestConvert<T> {

    private Class<T> targetClass;

    public ProtoBufferConvert(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T convert(Request request) {
        if (Objects.nonNull(request.data())) {
            return (T) request.data();
        }
        return ProtoBufUtils.deSerialize(request.byteData(), targetClass);
    }
}
