package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.Request;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InvokeMethodArgumentValuesImpl implements InvokeMethodArgumentValues<Request> {

    private ConvertManager convertManager;

    private Map<Integer, ParamConvert<?>[]> convert;

    public InvokeMethodArgumentValuesImpl(ConvertManager convertManager) {
        this.convertManager = convertManager;
        this.convert = new ConcurrentHashMap<>();
    }

    public void setConvert(Map<Integer, ParamConvert<?>[]> convert) {
        this.convert = convert;
    }

    public InvokeMethodArgumentValuesImpl(ConvertManager convertManager, Map<Integer, ParamConvert<?>[]> convert) {
        this.convertManager = convertManager;
        this.convert = convert;
    }

    private Object[] convert(Request request, ParamConvert[] converts) {
        Object[] values = new Object[]{converts.length};
        for (int i = 0; i < converts.length; i++) {
            values[i] = converts[i].convert(request);
        }
        return values;
    }

    private Object[] getMethodArgumentValues(Request request, HandlerMethod handlerMethod) throws Exception {

        int cmd = request.cmd();
        ParamConvert<?>[] converts = this.convert.get(cmd);
        if (Objects.isNull(converts)) {
            synchronized (convert) {
                if (Objects.isNull(this.convert.get(convert))) {
                    ParamConvert<?>[] params = buildArgumentValues(handlerMethod);
                    this.convert.put(cmd, params);
                    converts = params;
                }
            }
        }
        return convert(request, converts);
    }

    public ParamConvert<?>[] buildArgumentValues(HandlerMethod handlerMethod) {
        Class<?>[] parameters = handlerMethod.getParamsType();
        ParamConvert<?>[] params = new ParamConvert[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {

            Class<?> target = parameters[i];
            ParamConvert<?> convert = convertManager.getConvert(target);
            if (Objects.isNull(convert)) {
                throw new IllegalStateException(handlerMethod.getMethod().getName() + " param type not has convert");
            }
            params[i] = convert;
        }
        return params;
    }

    @Override
    public Object[] transfer(Request value, HandlerMethod handlerMethod) throws Exception {
        return this.getMethodArgumentValues(value, handlerMethod);
    }
}
