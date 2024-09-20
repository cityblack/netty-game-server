package com.lzh.game.framework.socket.core.invoke.convert;

import com.lzh.game.framework.socket.core.invoke.convert.impl.*;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
@Slf4j
public class DefaultInvokeMethodArgumentValues implements InvokeMethodArgumentValues, ConvertManager {

    private final Map<Integer, Convert<?>[]> convert;
    private List<Convert<?>> converts;

    public DefaultInvokeMethodArgumentValues() {
        this(new HashMap<>());
        var cs = Arrays.asList(new RequestParamConvert()
                , new ResponseConvert()
                , new SessionConvert()
                , new ProtocConvert());
        converts = new CopyOnWriteArrayList<>(cs);
    }

    public DefaultInvokeMethodArgumentValues(Map<Integer, Convert<?>[]> convert) {
        this.convert = convert;
    }

    private Object[] convert(Request request, Convert<?>[] converts) {
        Object[] values = new Object[converts.length];
        for (int i = 0; i < converts.length; i++) {
            values[i] = converts[i].convert(request);
        }
        return values;
    }

    private Convert<?> getTargetConvert(Class<?> targetConvert) {
        for (var requestConvert : this.converts) {
            if (requestConvert.match(targetConvert)) {
                return requestConvert;
            }
        }
        throw new ClassCastException("Can not find " + targetConvert.getName() + "'s convert.");
    }

    private Object[] getMethodArgumentValues(Request request, EnhanceMethodInvoke invoke) {
        return convert(request, getConvert(request.getMsgId(), invoke));
    }

    private Convert<?>[] getConvert(int msgId, EnhanceMethodInvoke invoke) {
        Convert<?>[] converts = this.convert.get(msgId);
        if (Objects.isNull(converts)) {
            synchronized (this) {
                if (!this.convert.containsKey(msgId)) {
                    Convert<?>[] tmp = buildArgumentValues(invoke);
                    this.convert.put(msgId, tmp);
                }
                return this.convert.get(msgId);
            }
        }
        return converts;
    }

    private Convert<?>[] buildArgumentValues(EnhanceMethodInvoke invoke) {
        Class<?>[] parameters = invoke.getParamsType();
        Convert<?>[] params = new Convert[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> target = parameters[i];
            params[i] = getTargetConvert(target);
        }
        return params;
    }

    @Override
    public Object[] transfer(Request request, EnhanceMethodInvoke handlerMethod) {
        return this.getMethodArgumentValues(request, handlerMethod);
    }

    @Override
    public void registerConvert(Convert<?> convert) {
        this.converts.add(convert);
    }

}
