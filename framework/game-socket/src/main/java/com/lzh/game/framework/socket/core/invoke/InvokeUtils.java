package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.annotation.Receive;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import com.lzh.game.framework.utils.bean.HandlerMethod;
import com.lzh.game.framework.utils.bean.MethodInvoke;
import com.lzh.game.framework.utils.bean.MethodInvokeUtils;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parse bean to invoker.
 * Agreement. Apart from the built-in parameters, there is only one additional parameter
 */
public class InvokeUtils {

    public static List<InvokeModel> parseBean(Object bean) {
        Class<?> clazz = bean.getClass();
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Receive.class))
                .map(method -> toModel(bean, method))
                .collect(Collectors.toList());
    }

    private static InvokeModel toModel(Object bean, Method method) {
        Receive receive = method.getAnnotation(Receive.class);
        if (Objects.isNull(receive)) {
            return null;
        }
        return parseTargetMethod(receive, bean, new HandlerMethod(bean, method));
    }

    /**
     * {@link com.lzh.game.framework.socket.core.protocol.message.Protocol}
     * {@link Receive}
     * @param receive
     * @param bean
     * @param method
     * @return
     */
    private static InvokeModel parseTargetMethod(Receive receive, Object bean, HandlerMethod method) {

        Protocol protocol = getProtoCol(method.getParamsType());
        if (receive.value() != 0 && Objects.nonNull(protocol)) {
            throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + " Duplicate definition @Receive and @Protocol");
        }
        boolean useSimpleProto = receive.value() != 0;

//        int msgId = receive.value() != 0 ? receive.value() : protocol.value();

//        List<Class<?>> protoParamType = Stream.of(method.getParamsType()).toList();

//        InvokeModel methodMapping = buildMapping(method, cmd);
        // Set method proto param
        if (!protoParamType.isEmpty()) {
            methodMapping.setParamClass(protoParamType.get(0));
        }

        return methodMapping;
    }

    //
    private static MessageDefine buildMethodMessageDefine(HandlerMethod method) {
        for (Class<?> type : method.getParamsType()) {
            if (type.isPrimitive()) {

            }
        }
    }

    private static Protocol getProtoCol(Class<?>[] types) {
        for (Class<?> type : types) {
            if (type.isAnnotationPresent(Protocol.class)) {
                return type.getAnnotation(Protocol.class);
            }
        }
        return null;
    }

    private static InvokeModel buildMapping(HandlerMethod method, int msgId) {
        InvokeModel methodMapping = new InvokeModel();
        methodMapping.setValue(msgId);
        methodMapping.setHandlerMethod(method);
        return methodMapping;
    }

    @Data
    @Accessors(chain = true)
    public static class InvokeModel {
        /**
         * cmd id
         */
        private int value;

        private EnhanceMethodInvoke handlerMethod;

        private Class<?> paramClass;

        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }
            if (!(obj instanceof InvokeModel)) {
                return false;
            }
            if (((InvokeModel) obj).getValue() == this.getValue()) {
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.value);
        }

    }

    private InvokeUtils() {}

}
