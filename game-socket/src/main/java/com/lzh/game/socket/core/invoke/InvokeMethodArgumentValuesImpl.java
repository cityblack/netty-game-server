package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.Request;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InvokeMethodArgumentValuesImpl implements InvokeMethodArgumentValues<Request> {

    private ConvertManager convertManager;

    private Map<Integer, ParamConvert<?>[]> convert = new ConcurrentHashMap<>();

    public InvokeMethodArgumentValuesImpl(ConvertManager convertManager) {
        this.convertManager = convertManager;
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
        ParamConvert[] converts = this.convert.get(cmd);
        if (Objects.isNull(converts)) {
            synchronized (handlerMethod) {
                if (Objects.isNull(this.convert.get(handlerMethod))) {
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
                    this.convert.put(cmd, params);
                    converts = params;
                }
            }
        }

        return convert(request, converts);
    }

    @Override
    public Object[] transfer(Request value, HandlerMethod handlerMethod) throws Exception {
        return this.getMethodArgumentValues(value, handlerMethod);
    }
}
