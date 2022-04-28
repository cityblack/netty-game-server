package com.lzh.game.socket.core.invoke;

import com.lzh.game.common.bean.EnhanceHandlerMethod;
import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.socket.annotation.RequestMapping;
import com.lzh.game.socket.annotation.ResponseMapping;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvokeUtils {

    public static List<InvokeModel> parseBean(Object bean) {
        return parseBean(bean, InnerParamBindHandler.EMPTY);
    }

    public static List<InvokeModel> parseBean(Object bean, InnerParamBindHandler paramHandler) {
        Class<?> clazz = bean.getClass();
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .map(method -> toModel(bean, method, paramHandler))
                .collect(Collectors.toList());
    }

    private static InvokeModel toModel(Object bean, Method method, InnerParamBindHandler paramHandler) {
        EnhanceHandlerMethod invoke = new EnhanceHandlerMethod(bean, method);
        RequestMapping mapping = method.getAnnotation(RequestMapping.class);
        return parseTargetMethod(mapping, invoke, paramHandler);
    }

    private static InvokeModel parseTargetMethod(RequestMapping mapping, HandlerMethod method, InnerParamBindHandler paramHandler) {
        int cmd = mapping.value();

        long protoParam = Stream.of(method.getMethodParameters())
                .filter(e -> !paramHandler.isInnerParam(e))
                .count();
        // Just allow one mapping proto class
        if (protoParam > 1) {
            throw new IllegalArgumentException(method.getBean().getClass().getSimpleName() + " " + method.getMethod().getName() + " has multi map protocol class.");
        }
        InvokeModel methodMapping = buildMapping(method, cmd);

        if (!method.isVoid()) {
            ResponseMapping responseMapping = method.getMethodAnnotation(ResponseMapping.class);
            if (Objects.isNull(responseMapping)) {
                throw new IllegalArgumentException(cmd + " protocol return value is not null. so that must use @Response to map the method ");
            }
            int response = responseMapping.value();
            methodMapping.setResponse(response);
        }

        return methodMapping;
    }

    private static InvokeModel buildMapping(HandlerMethod method, int request) {
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

        private int response;

        private HandlerMethod handlerMethod;

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
}
