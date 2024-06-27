package com.lzh.game.framework.socket.utils;

import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.core.protocol.message.SimpleProtoc;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import com.lzh.game.framework.utils.bean.HandlerMethod;
import com.lzh.game.framework.utils.bean.MethodInvoke;
import com.lzh.game.framework.utils.bean.MethodInvokeUtils;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.IntegerMemberValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Parse bean to invoker.
 * Agreement. Apart from the built-in parameters, there is only one additional parameter
 */
public class InvokeUtils {

    public static Object[] EMPTY_OBJECT = new Object[0];

    private static ClassPool pl;

    static {
        pl = ClassPool.getDefault();
        pl.importPackage("java.lang");
    }

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
        try {
            Protocol protocol = getProtoCol(method.getParamsType());
            if (receive.value() != 0 && Objects.nonNull(protocol)) {
                throw new IllegalArgumentException(bean.getClass().getName() + "." + method.getName() + " Duplicate definition @Receive and @Protocol");
            }
            boolean useSimpleProto = receive.value() != 0;
            int msgId = useSimpleProto ? receive.value() : protocol.value();
            var model = new InvokeModel();
            if (useSimpleProto) {
                model.setNewProtoClass(buildMethodClass(msgId, method));
            }
            model.setValue(msgId);
            var enhance = MethodInvokeUtils.enhanceInvoke(bean, method.getMethod());
            model.setHandlerMethod(new EnhanceInvokeImpl(method, enhance));
            return model;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static Class<?> buildMethodClass(int msgId, HandlerMethod method) {
        List<Class<?>> list = new ArrayList<>();
        for (Class<?> type : method.getParamsType()) {
            if (!type.isPrimitive()) {
                throw new IllegalArgumentException("Simple proto field type is not primitive.");
            }
            list.add(type);
        }
        var name = "%dSimpleProtoClass".formatted(msgId);
        return buildClass(list, name, msgId);
    }

    // use javassist build class
    private static Class<?> buildClass(List<Class<?>> fields, String className, int msgId) {
        try {
            CtClass enhance = pl.makeClass(className);
            enhance.addInterface(pl.getCtClass(SimpleProtoc.class.getName()));

            List<String> fieldNames = new ArrayList<>(fields.size());

            for (int i = 0; i < fields.size(); i++) {
                Class<?> field = fields.get(i);
                String fieldName = "var%d".formatted(i);
                CtField beanField = new CtField(pl.get(field.getTypeName()), fieldName, enhance);
                enhance.addField(beanField);
                fieldNames.add(fieldName);
            }

            // SimpleProtoc impl
            CtMethod getValues = CtNewMethod.make(buildGetValueMethodBody(fieldNames), enhance);
            enhance.addMethod(getValues);

            var constant = enhance.getClassFile().getConstPool();
            AnnotationsAttribute attr = new AnnotationsAttribute(constant, AnnotationsAttribute.visibleTag);
            Annotation annot = new Annotation(constant, pl.getCtClass(Protocol.class.getName()));
            annot.addMemberValue("value", new IntegerMemberValue(constant, msgId));
            annot.addMemberValue("serializeType", new IntegerMemberValue(constant, Constant.DEFAULT_SERIAL_SIGN));
            enhance.getClassFile().addAttribute(attr);

            return enhance.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildGetValueMethodBody(List<String> fieldNames) {
        var method = "public Object[] getFieldValues() { \t\n";
        if (!fieldNames.isEmpty()) {
            method += "return " + String.join(",", fieldNames) + ";\t\n";
        } else {
            method += "return null;\t\n";
        }
        method += "}\t\n";
        return method;
    }

    private static Protocol getProtoCol(Class<?>[] types) {
        for (Class<?> type : types) {
            if (type.isAnnotationPresent(Protocol.class)) {
                return type.getAnnotation(Protocol.class);
            }
        }
        return null;
    }

    @Data
    @Accessors(chain = true)
    public static class InvokeModel {
        /**
         * cmd id
         */
        private int value;

        private EnhanceMethodInvoke handlerMethod;

        private Class<?> newProtoClass;

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

    @AllArgsConstructor
    public static class EnhanceInvokeImpl implements EnhanceMethodInvoke {

        private HandlerMethod method;

        private MethodInvoke invoke;

        @Override
        public Class<?>[] getParamsType() {
            return method.getParamsType();
        }

        @Override
        public Class<?> getReturnType() {
            return method.getReturnType();
        }

        @Override
        public boolean isVoid() {
            return method.isVoid();
        }

        @Override
        public Object invoke(Object... args) {
            return invoke.invoke(args);
        }
    }

    private InvokeUtils() {}

}
