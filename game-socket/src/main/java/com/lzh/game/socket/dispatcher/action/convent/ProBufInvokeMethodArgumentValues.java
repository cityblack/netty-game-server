package com.lzh.game.socket.dispatcher.action.convent;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.dispatcher.ProtoUtil;
import com.lzh.game.socket.exchange.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;


/**
 * Use proBuf to change request data to method arguments value.
 */
public class ProBufInvokeMethodArgumentValues implements InvokeMethodArgumentValues<Request> {

    private InnerParamDataBindHandler innerParamDataBindHandler;

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public ProBufInvokeMethodArgumentValues(InnerParamDataBindHandler innerParamDataBindHandler) {
        this.innerParamDataBindHandler = innerParamDataBindHandler;
    }

    private Object dataToObject(byte[] data, Class<?> target) {
        return ProtoUtil.deSerialize(data, target);
    }

    private Object[] getMethodArgumentValues(Request<byte[]> request, HandlerMethod handlerMethod) throws Exception {

        MethodParameter[] parameters = handlerMethod.getMethodParameters();
        Object[] args = new Object[parameters.length];

        boolean targetChange = false;

        for(int i = 0; i < parameters.length; ++i) {

            MethodParameter parameter = parameters[i];

            parameter.initParameterNameDiscovery(getParameterNameDiscoverer());

            if (innerParamDataBindHandler.isInnerParam(parameter)) {

                args[i] = innerParamDataBindHandler.conventData(request, parameter);

            } else if (!targetChange) {
                args[i] = dataToObject(request.data(), parameter.getParameterType());

                targetChange = true;

            } else if (args[i] == null) {
                throw new IllegalStateException("Could not resolve convent parameter at index "
                        + parameter.getParameterIndex() +
                        " in " + parameter.getExecutable().toGenericString() + ": "
                        + this.getArgumentResolutionErrorMessage("No suitable resolver for", i, handlerMethod));
            }
        }

        return args;
    }


    @Override
    public Object[] transfer(Request value, HandlerMethod handlerMethod) throws Exception {
        return this.getMethodArgumentValues(value, handlerMethod);
    }

    public ParameterNameDiscoverer getParameterNameDiscoverer() {
        return parameterNameDiscoverer;
    }

    protected String getArgumentResolutionErrorMessage(String text, int index, HandlerMethod handlerMethod) {
        Class<?> paramType = handlerMethod.getMethodParameters()[index].getParameterType();
        return text + " argument " + index + " of type '" + paramType.getName() + "'";
    }
}
