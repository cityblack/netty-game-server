package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.socket.annotation.RequestMapping;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvokeUtils {

    public static List<InvokeModel> parseBean(Object bean) {
        return parseBean(bean, Collections.emptySet());
    }

    public static List<InvokeModel> parseBean(Object bean, Set<Class<?>> inner) {
        Class<?> clazz = bean.getClass();
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .map(method -> toModel(bean, method, inner))
                .collect(Collectors.toList());
    }

    private static InvokeModel toModel(Object bean, Method method, Set<Class<?>> inner) {
        EnhanceHandlerMethod invoke = new EnhanceHandlerMethod(bean, method);
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        return parseTargetMethod(mapping, invoke, inner);
    }

    private static InvokeModel parseTargetMethod(RequestMapping mapping, EnhanceHandlerMethod method, Set<Class<?>> inner) {
        int cmd = mapping.value();

        List<Class<?>> protoParamType = Stream.of(method.getParamsType())
                .filter(e -> !inner.contains(e))
                .collect(Collectors.toList());
        // Just allow one mapping proto class
        if (protoParamType.size() > 1) {
            throw new IllegalArgumentException(method.getBean().getClass().getSimpleName() + " " + method.getMethod().getName() + " has multi map protocol class.");
        }
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

        public boolean hasParam() {
            return Objects.nonNull(paramClass);
        }

        public boolean isReturnValue() {
            return false;
//            return this.handlerMethod.getReturnType() instanceof Void
        }
    }
}
