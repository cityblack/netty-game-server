package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.Request;

public class ProtoBufferConvert<T> implements ParamConvert<T> {

    private Class<T> targetClass;

    public ProtoBufferConvert(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public T convert(Request request) {
        return ProtoBufUtils.deSerialize(request.byteData(), targetClass);
    }
}
