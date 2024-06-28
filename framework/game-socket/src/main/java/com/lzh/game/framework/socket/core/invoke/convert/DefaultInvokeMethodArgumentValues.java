package com.lzh.game.framework.socket.core.invoke.convert;

import com.lzh.game.framework.socket.core.invoke.convert.impl.*;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.message.SimpleProtoc;
import com.lzh.game.framework.socket.utils.InvokeUtils;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@link InvokeUtils#parseBean(Object)}
 */
@Slf4j
public class DefaultInvokeMethodArgumentValues implements InvokeMethodArgumentValues, RequestConvertManager {

    private final Map<Integer, RequestConvert<?>[]> convert;
    private List<RequestConvert<?>> converts;

    public DefaultInvokeMethodArgumentValues() {
        this(new HashMap<>());
        var cs = Arrays.asList(new RequestParamConvert()
                , new ResponseConvert(), new SessionConvert(), new ProtocConvert());
        converts = new CopyOnWriteArrayList<>(cs);
    }

    public DefaultInvokeMethodArgumentValues(Map<Integer, RequestConvert<?>[]> convert) {
        this.convert = convert;
    }

    private Object[] convert(Request request, RequestConvert<?>[] converts) {
        Object[] values = new Object[]{converts.length};
        for (int i = 0; i < converts.length; i++) {
            values[i] = converts[i].convert(request);
        }
        return values;
    }

    private RequestConvert<?> getTargetConvert(Class<?> targetConvert) {
        for (var requestConvert : this.converts) {
            if (requestConvert.match(targetConvert)) {
                return requestConvert;
            }
        }
        throw new ClassCastException("Can not find " + targetConvert.getName() + "'s convert.");
    }

    private Object[] getMethodArgumentValues(Request request, EnhanceMethodInvoke invoke) {
        return convert(request, getConvert(request.getMsgId(), request, invoke));
    }

    private RequestConvert<?>[] getConvert(int msgId, Request request, EnhanceMethodInvoke invoke) {
        RequestConvert<?>[] converts = this.convert.get(msgId);
        if (Objects.isNull(converts)) {
            synchronized (this) {
                if (!this.convert.containsKey(msgId)) {
                    RequestConvert<?>[] tmp = buildArgumentValues(request, invoke);
                    this.convert.put(msgId, tmp);
                }
                return this.convert.get(msgId);
            }
        }
        return converts;
    }

    private RequestConvert<?>[] buildArgumentValues(Request request, EnhanceMethodInvoke invoke) {
        Class<?>[] parameters = invoke.getParamsType();
        RequestConvert<?>[] params = new RequestConvert[parameters.length];
        if (request.getData() instanceof SimpleProtoc protoc) {
            var fields = protoc.getFieldValues();
            int index = 0;
            for (int i = 0; i < parameters.length; i++) {
                Class<?> target = parameters[i];
                if (InvokeUtils.isSimpleProtocParam(target)) {
                    params[i] = new SimpleProtocConvert(index++, fields);
                } else {
                    params[i] = getTargetConvert(target);
                }
            }
        } else {
            for (int i = 0; i < parameters.length; i++) {
                Class<?> target = parameters[i];
                params[i] = getTargetConvert(target);
            }
        }

        return params;
    }

    @Override
    public Object[] transfer(Request request, EnhanceMethodInvoke handlerMethod) {
        return this.getMethodArgumentValues(request, handlerMethod);
    }

    @Override
    public void registerConvert(RequestConvert<?> convert) {
        this.converts.add(convert);
    }

}
