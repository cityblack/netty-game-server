package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.core.invoke.convert.SysParam;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.ComposeProtoc;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.utils.bean.EnhanceMethodInvoke;
import com.lzh.game.framework.utils.bean.HandlerMethod;
import com.lzh.game.framework.utils.bean.MethodInvoke;
import com.lzh.game.framework.utils.bean.MethodInvokeUtils;
import javassist.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2024-07-02 11:51
 **/
@Slf4j
public class InvokeBeanHelper {

    private static ClassPool pl;

    private static final Map<String, Class<?>> PROTOC_CLASS = new ConcurrentHashMap<>();

    static {
        pl = ClassPool.getDefault();
        pl.importPackage("java.lang");
    }

    private InvokeSupport invokeSupport;

    private MessageManager messageManager;

    public InvokeBeanHelper(InvokeSupport invokeSupport, MessageManager messageManager) {
        this.invokeSupport = invokeSupport;
        this.messageManager = messageManager;
    }

    public void parseBean(Object bean, Predicate<Method> filter) {
        Class<?> clazz = bean.getClass();
        Stream.of(clazz.getDeclaredMethods())
                .filter(filter)
                .map(method -> toModel(bean, method))
                .filter(Objects::nonNull)
                .forEach(e -> {
                    invokeSupport.register(e.getValue(), e.getHandlerMethod());
                    for (MessageDefine define : e.defines) {
                        messageManager.registerMessage(define);
                    }
                    for (Class<?> clz : e.protocol) {
                        messageManager.addMessage(clz);
                    }
                });
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
     *
     * @param receive
     * @param bean
     * @param method
     * @return
     */
    private static InvokeModel parseTargetMethod(Receive receive, Object bean, HandlerMethod method) {
        boolean useCompose = receive.value() != 0;
        var protocol = getProtoCol(method.getParamsType());
        // If compose protocol not used , Can only have one parameter
        if (!useCompose && protocol.size() != 1) {
            throw new IllegalArgumentException("If compose protocol not used , Can only have one parameter.");
        }
        short msgId = useCompose ? receive.value() : protocol.get(0).value();
        boolean hasParam = method.getParamsType().length > 0;
        try {
            var model = new InvokeModel();
            if (!hasParam) {
                model.defines.add(new MessageDefine(msgId, Void.class));
            } else if (useCompose) {
                if (method.getParamsType().length == 1) {
                    var type = method.getParamsType()[0];
                    model.defines.add(new MessageDefine(msgId, type));
                } else {
                    var newType = getOrBuildProtocolClass(method);
                    model.defines.add(new MessageDefine(msgId, newType));
                }
            }
            for (Class<?> clz : method.getParamsType()) {
                parseProtocol(clz, model.getProtocol());
            }

            model.setValue(msgId);
            var enhance = MethodInvokeUtils.enhanceInvoke(bean, method.getMethod());
            model.setHandlerMethod(new EnhanceInvokeImpl(method, enhance));
            return model;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void parseProtocol(Class<?> clz, List<Class<?>> protocols) {
        if (!clz.isAnnotationPresent(Protocol.class)) {
            return;
        }
        protocols.add(clz);
        for (Field field : clz.getFields()) {
            var type = field.getType();
            if (!type.isAnnotationPresent(Protocol.class)) {
                continue;
            }
            parseProtocol(clz, protocols);
        }
    }

    private static Class<?> getOrBuildProtocolClass(HandlerMethod method) {
        var key = Arrays.stream(method.getParamsType())
                .map(Class::getName)
                .collect(Collectors.joining());
        return PROTOC_CLASS.computeIfAbsent(key, k -> buildProtocolClass(k, method));
    }

    /**
     * Use primitive or @Protocol method's param to build a new class
     *
     * @param method
     * @return
     */
    private static Class<?> buildProtocolClass(String className, HandlerMethod method) {
        List<Class<?>> list = new ArrayList<>();
        for (Class<?> type : method.getParamsType()) {
            if (!isSysParam(type)) {
                continue;
            }
            list.add(type);
        }
        return buildClass(list, className);
    }

    // use javassist build class
    private static Class<?> buildClass(List<Class<?>> fields, String className) {
        try {
            CtClass enhance = pl.makeClass(className);
            enhance.addInterface(pl.getCtClass(ComposeProtoc.class.getName()));

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

            log.debug("Build class: {}", className);
//            enhance.writeFile(InvokeUtils.class.getResource("./").getFile());
            return enhance.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildGetValueMethodBody(List<String> fieldNames) {
        var method = "public Object[] getFieldValues() { \t\n";
        if (!fieldNames.isEmpty()) {
            method += "return new Object[]{" + String.join(",", fieldNames) + "};\t\n";
        } else {
            method += "return null;\t\n";
        }
        method += "}\t\n";
        return method;
    }

    private static List<Protocol> getProtoCol(Class<?>[] types) {
        List<Protocol> list = new ArrayList<>();
        for (Class<?> type : types) {
            if (type.isAnnotationPresent(Protocol.class)) {
                list.add(type.getAnnotation(Protocol.class));
            }
        }
        return list;
    }

    public static boolean isSysParam(Class<?> type) {
        return type.isAnnotationPresent(SysParam.class);
    }

    @Data
    @Accessors(chain = true)
    private static class InvokeModel {
        /**
         * cmd id
         */
        private short value;

        private EnhanceMethodInvoke handlerMethod;

        private List<MessageDefine> defines = new ArrayList<>();

        private List<Class<?>> protocol = new ArrayList<>();

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

}
