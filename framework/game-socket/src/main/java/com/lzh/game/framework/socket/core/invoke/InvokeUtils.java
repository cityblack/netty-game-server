package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.annotation.RequestMapping;
import lombok.Data;

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
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .map(method -> toModel(bean, method))
                .collect(Collectors.toList());
    }

    private static InvokeModel toModel(Object bean, Method method) {
        EnhanceHandlerMethod invoke = new EnhanceHandlerMethod(bean, method);
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        return parseTargetMethod(mapping, invoke);
    }

    private static InvokeModel parseTargetMethod(RequestMapping mapping, EnhanceHandlerMethod method) {
        int cmd = mapping.value();

        List<Class<?>> protoParamType = Stream.of(method.getParamsType())
                .collect(Collectors.toList());
        InvokeModel methodMapping = buildMapping(method, cmd);
        // Set method proto param
        if (!protoParamType.isEmpty()) {
            methodMapping.setParamClass(protoParamType.get(0));
        }

        return methodMapping;
    }

    private static InvokeModel buildMapping(EnhanceHandlerMethod method, int request) {
        InvokeModel methodMapping = new InvokeModel();
        methodMapping.setValue(request);
        methodMapping.setHandlerMethod(method);
        return methodMapping;
    }

    @Data
    public static class InvokeModel {
        /**
         * cmd id
         */
        private int value;

        private EnhanceHandlerMethod handlerMethod;

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
