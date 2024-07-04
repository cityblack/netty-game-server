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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2024-07-02 11:51
 **/
@Slf4j
public class InvokeBeanHelper {

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
        if (log.isDebugEnabled()) {
            log.debug("Invoke size:{}", messageManager.count());
        }
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
        if (!type.isAnnotationPresent(Protocol.class)) {
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

            model.message = parseParams(msgId, getMethodParamTypes(method));

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

    /**
     * Filter @SysParam param type
     *
     * @param method
     * @return
     */
    private static Class<?>[] getMethodParamTypes(HandlerMethod method) {
        var paramTypes = method.getParamsType();
        var anno = method.getParameterAnnotations();
        var list = new ArrayList<>();
        for (int i = 0; i < paramTypes.length; i++) {
            boolean hasSysParam = Arrays.stream(anno[i])
                    .anyMatch(e -> e.annotationType() == SysParam.class);
            if (hasSysParam) {
                continue;
            }
            list.add(paramTypes[i]);
        }
        return list.toArray(Class<?>[]::new);
    }

    private static void parseProtocol(Class<?> clz, Set<Class<?>> protocols) {
        if (protocols.contains(clz)) {
            return;
        }
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

        return PROTOC_CLASS.computeIfAbsent(key, k -> buildProtocolClass(params, false));
    }

    public static Class<?> buildProtocolClass(Class<?>[] params, boolean writeFile) {
        List<Class<?>> list = new ArrayList<>();
        for (Class<?> type : params) {
            if (isSysParam(type)) {
                continue;
            }
            list.add(type);
        }
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Compose protocol params is null.");
        }
        var className = "ComposeProtocol%d".formatted(PROTOC_CLASS.size() + 1);
        return buildClass(list, className, writeFile);
    }

    // use javassist build class
    private static Class<?> buildClass(List<Class<?>> fields, String className, boolean writeFile) {
        try {
            CtClass enhance = pl.makeClass(className);
            var compose = pl.getCtClass(ComposeProtoc.class.getName());
            enhance.addInterface(compose);

            List<String> fieldNames = new ArrayList<>(fields.size());
            CtClass[] paramTypes = new CtClass[fields.size()];
            StringBuilder allArgsBody = new StringBuilder("{\t\n ");

            for (int i = 0; i < fields.size(); i++) {
                Class<?> field = fields.get(i);
                String fieldName = "var%d".formatted(i);
                var type = pl.get(field.getTypeName());
                CtField beanField = new CtField(type, fieldName, enhance);
                beanField.setModifiers(Modifier.PUBLIC);
                enhance.addField(beanField);
                fieldNames.add(fieldName);
                paramTypes[i] = type;
                allArgsBody.append("this.")
                        .append(fieldName)
                        .append("= $%d;\t\n".formatted(i + 1));

            }
            allArgsBody.append("}\t\n");

            // All args constructor
            enhance.addConstructor(CtNewConstructor.make(paramTypes, new CtClass[0], allArgsBody.toString(), enhance));
            // Default constructor
            enhance.addConstructor(CtNewConstructor.defaultConstructor(enhance));

            // ComposeProtoc impl
            CtMethod getValues = CtNewMethod.make(buildGetValueMethodBody(fields, fieldNames), enhance);
            enhance.addMethod(getValues);

            log.debug("Build class: {}", className);
            if (writeFile) {
                enhance.writeFile(InvokeBeanHelper.class.getResource("./").getFile());
            }
            return enhance.toClass();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildGetValueMethodBody(List<Class<?>> types, List<String> fieldNames) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            var name = fieldNames.get(i);
            var type = types.get(i);
            if (type.isPrimitive()) {
                builder.append(VALUE_MAP.get(type).apply(name));
            } else {
                builder.append(name);
            }
            if (i != fieldNames.size() - 1) {
                builder.append(",");
            }
        }
        return """
                public Object[] getFieldValues() {
                    return new Object[]{%s};
                }
                """.formatted(builder.toString());
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

        private Set<Class<?>> protocol = new HashSet<>();
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

    private final static ClassPool pl;

    private static final Map<String, Class<?>> PROTOC_CLASS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Function<String, String>> VALUE_MAP = new HashMap<>();

    static {
        pl = ClassPool.getDefault();
        pl.importPackage("java.lang");
        VALUE_MAP.put(int.class, "Integer.valueOf(%s)"::formatted);
        VALUE_MAP.put(double.class, "Double.valueOf(%s)"::formatted);
        VALUE_MAP.put(float.class, "Float.valueOf(%s)"::formatted);
        VALUE_MAP.put(long.class, "Long.valueOf(%s)"::formatted);
        VALUE_MAP.put(char.class, "String.valueOf(%s)"::formatted);
    }
}
