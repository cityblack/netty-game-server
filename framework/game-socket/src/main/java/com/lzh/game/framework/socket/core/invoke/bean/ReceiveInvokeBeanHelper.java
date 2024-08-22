package com.lzh.game.framework.socket.core.invoke.bean;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.utils.bean.HandlerMethod;
import com.lzh.game.framework.utils.bean.MethodInvokeUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Last param must be protoc
 *
 * @author zehong.l
 * @Receive test(@ System Request request, Object param)
 * @Receive test(Object param)
 * @since 2024-08-21 16:08
 **/
public class ReceiveInvokeBeanHelper implements InvokeBeanHelper {

    @Override
    public InvokeModel parseBean(Object bean, Method method, BootstrapContext<?> context) {
        return parseReceiveMethod(bean, new HandlerMethod(bean, method), context);
    }

    @Override
    public boolean match(Object bean, Method method) {
        return method.isAnnotationPresent(Receive.class);
    }

    /**
     * {@link com.lzh.game.framework.socket.core.protocol.message.Protocol}
     * {@link Receive}
     *
     * @param bean
     * @param method
     * @return
     */
    private InvokeModel parseReceiveMethod(Object bean, HandlerMethod method, BootstrapContext<?> context) {

        var protocol = getProtoCol(method);
        // If compose protocol not used , Can only have one parameter
        if (protocol.size() != 1) {
            throw new IllegalArgumentException("Can only have one parameter.");
        }
        var value = protocol.get(0);
        var anno = value.getAnnotation(Protocol.class);
        short msgId = anno.value();
        var protocols = new HashSet<Class<?>>();

        try {
            protocols.add(value);
            if (!method.isVoid()) {
                var returnType = method.getReturnType();
                if (returnType.isAnnotationPresent(Protocol.class)) {
                    protocols.add(returnType);
                } else if (Objects.isNull(context.getMessageManager().findDefined(returnType))) {
                    throw new IllegalArgumentException("Not support the return type :" + returnType.getName());
                }
            }

            var enhance = MethodInvokeUtils.enhanceInvoke(bean, method.getMethod());
            return InvokeModel.of()
                    .setMsgId(msgId)
                    .setHandlerMethod(new EnhanceInvokeImpl(method, enhance))
                    .setProtocol(protocols);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private List<Class<?>> getProtoCol(HandlerMethod method) {
        var types = method.getParamsType();
        var anno = method.getParameterAnnotations();
        List<Class<?>> list = new ArrayList<>(types.length);
        for (int i = 0; i < types.length; i++) {
            var type = types[i];
            if (InvokeBeanHelperHandler.isSystemParam(anno[i])) {
                continue;
            }
            if (type.isAnnotationPresent(Protocol.class)) {
                list.add(type);
            } else {
                throw new IllegalArgumentException("Param is not @SysParam or @Protocol.");
            }
        }
        return list;
    }
}
