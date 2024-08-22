package com.lzh.game.framework.socket.core.invoke.bean;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.invoke.convert.SysParam;
import com.lzh.game.framework.socket.core.protocol.message.ComposeProtoc;
import com.lzh.game.framework.utils.bean.HandlerMethod;
import javassist.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zehong.l
 * @since 2024-08-21 17:44
 **/
@Slf4j
public class ComposeInvokeHelper implements InvokeBeanHelper {

    @Override
    public InvokeModel parseBean(Object bean, Method method, BootstrapContext<?> context) {
        return null;
    }

    @Override
    public boolean match(Object bean, Method method) {
        return false;
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
            if (type.isAnnotationPresent(SysParam.class)) {
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
                enhance.writeFile(InvokeBeanHelperHandler.class.getResource("./").getFile());
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
