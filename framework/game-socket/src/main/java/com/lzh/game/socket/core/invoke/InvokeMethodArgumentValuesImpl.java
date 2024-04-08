package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class InvokeMethodArgumentValuesImpl implements InvokeMethodArgumentValues {

    private RequestConvertManager convertManager;
    //
    private Map<Integer, RequestConvert<?>[]> convert;

    public InvokeMethodArgumentValuesImpl(RequestConvertManager convertManager) {
        this.convertManager = convertManager;
        this.convert = new HashMap<>();
    }

    private Object[] convert(Request request, RequestConvert<?>[] converts) {
        Object[] values = new Object[]{converts.length};
        for (int i = 0; i < converts.length; i++) {
            values[i] = converts[i].convert(request);
        }
        return values;
    }

    private RequestConvert<?> getTargetConvert(Class<?> targetConvert) {
        return convertManager.getOrCreateDefaultConvert(targetConvert);
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
}
