package com.lzh.game.framework.socket.core.invoke;

import com.lzh.game.framework.socket.core.invoke.convert.SysParam;
import com.lzh.game.framework.socket.core.invoke.support.InvokeSupport;
import com.lzh.game.framework.socket.core.protocol.message.ComposeProtoc;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.utils.Constant;
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
                    if (Objects.nonNull(e.message.define)) {
                        messageManager.registerMessage(e.message.define);
                    }
                    for (Class<?> clz : e.message.protocol) {
                        messageManager.addMessage(clz);
                    }
                });
    }

    public void parseMessage(short msgId, Object... params) {
        var types = Stream.of(params)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);
        var msg = parseParams(msgId, types);
        if (Objects.nonNull(msg.define)) {
            messageManager.registerMessage(msg.define);
        }
        for (Class<?> clz : msg.protocol) {
            messageManager.addMessage(clz);
        }
    }

    private static InvokeModel toModel(Object bean, Method method) {
        Receive receive = method.getAnnotation(Receive.class);
        if (Objects.isNull(receive)) {
            return null;
        }
        return parseTargetMethod(receive, bean, new HandlerMethod(bean, method));
    }

    private static Message parseParams(short msgId, Class<?>[] types) {
        var message = new Message();
        var type = getOrBuildProtocolClass(types);
        if (type.isAnnotationPresent(Protocol.class)) {
            message.protocol.add(type);
        } else {
            message.define = new MessageDefine()
                    .setMsgId(msgId)
                    .setFieldTypes(types)
                    .setSerializeType(Constant.DEFAULT_SERIAL_SIGN)
                    .setMsgClass(type);
            if (ComposeProtoc.class.isAssignableFrom(type)) {
                try {
                    message.define.setCompose(true);
                    message.define.setAllArgsConstructor(type.getConstructor(types));
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (Class<?> clz : types) {
            // Traversal class's fields to find @Protocol class that one add to manager
            parseProtocol(clz, message.protocol);
        }
        return message;
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
        try {
            var model = new InvokeModel();
            model.message = parseParams(msgId, method.getParamsType());

            if (!method.isVoid() && method.getReturnType().isAnnotationPresent(Protocol.class)) {
                model.message.getProtocol().add(method.getReturnType());
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

    public static Class<?> getOrBuildProtocolClass(HandlerMethod method) {
        return getOrBuildProtocolClass(method.getParamsType());
    }

    public static Class<?> getOrBuildProtocolClass(Class<?>[] params) {
        if (params.length == 0) {
            return Void.class;
        }
        if (params.length == 1) {
            return params[0];
        }
        var key = Arrays.stream(params)
                .map(Class::getName)
                .collect(Collectors.joining());
        return PROTOC_CLASS.computeIfAbsent(key, k -> buildProtocolClass(k, params));
    }

    private static Class<?> buildProtocolClass(String className, Class<?>[] params) {
        List<Class<?>> list = new ArrayList<>();
        for (Class<?> type : params) {
            if (isSysParam(type)) {
                continue;
            }
            list.add(type);
        }
        return buildClass(list, className);
    }

    // use javassist build class
    public static Class<?> buildClass(List<Class<?>> fields, String className) {
        try {
            CtClass enhance = pl.makeClass(className);
            enhance.addInterface(pl.getCtClass(ComposeProtoc.class.getName()));

            List<String> fieldNames = new ArrayList<>(fields.size());
            CtClass[] paramTypes = new CtClass[fields.size()];
            for (int i = 0; i < fields.size(); i++) {
                Class<?> field = fields.get(i);
                String fieldName = "var%d".formatted(i);
                var type = pl.get(field.getTypeName());
                CtField beanField = new CtField(type, fieldName, enhance);
                enhance.addField(beanField);
                fieldNames.add(fieldName);
                paramTypes[i] = type;
            }

            // All args constructor
            enhance.addConstructor(CtNewConstructor.make(paramTypes, new CtClass[0], enhance));
            // Default constructor
            enhance.addConstructor(CtNewConstructor.defaultConstructor(enhance));

            // ComposeProtoc impl
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

        private Message message;

    }

    @Data
    private static class Message {

        private MessageDefine define;

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
