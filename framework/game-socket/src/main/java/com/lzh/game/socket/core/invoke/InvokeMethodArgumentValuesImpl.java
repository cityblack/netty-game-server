package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.core.message.NetMessage;
import com.lzh.game.socket.core.protocol.Request;
import com.lzh.game.socket.core.protocol.Response;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class InvokeMethodArgumentValuesImpl implements InvokeMethodArgumentValues, RequestConvertManager {

    private final Map<Class<?>, RequestConvert<?>> convertContain = new HashMap<>();

    {
        registerConvert(Session.class, Request::getSession);
        registerConvert(Request.class, (r) -> r);
        registerConvert(Response.class, r -> r.getResponse());
        registerConvert(NetMessage.class, r -> r.getData());
    }


    public InvokeMethodArgumentValuesImpl() {
        this(new ConcurrentHashMap<>());
    }

    public InvokeMethodArgumentValuesImpl(Map<Integer, RequestConvert<?>[]> convert) {
        this.convert = convert;
    }

    //
    private final Map<Integer, RequestConvert<?>[]> convert;

    private Object[] convert(Request request, RequestConvert<?>[] converts) {
        Object[] values = new Object[]{converts.length};
        for (int i = 0; i < converts.length; i++) {
            values[i] = converts[i].convert(request);
        }
        return values;
    }

    private RequestConvert<?> getTargetConvert(Class<?> targetConvert) {
        return getOrCreateDefaultConvert(targetConvert);
    }

    private Object[] getMethodArgumentValues(Request request, HandlerMethod handlerMethod) throws Exception {
        int msgId = request.getMsgId();
        RequestConvert<?>[] cs = this.convert.get(msgId);
        if (Objects.isNull(cs)) {
            synchronized (this) {
                if (!this.convert.containsKey(msgId)) {
                    RequestConvert<?>[] tmp = buildArgumentValues(handlerMethod);
                    this.convert.put(msgId, tmp);
                    cs = tmp;
                }
            }
        }
        return convert(request, cs);
    }

    private RequestConvert<?>[] buildArgumentValues(HandlerMethod handlerMethod) {
        Class<?>[] parameters = handlerMethod.getParamsType();
        RequestConvert<?>[] params = new RequestConvert[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> target = parameters[i];
            params[i] = getTargetConvert(target);
        }
        return params;
    }

    @Override
    public Object[] transfer(Request request, HandlerMethod handlerMethod) throws Exception {
        return this.getMethodArgumentValues(request, handlerMethod);
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
//                    registerConvert(clazz, new ProtoBufferConvert<>(clazz));
                    return getConvert(clazz);
                }
            }
        }
        return convert;
    }
}
