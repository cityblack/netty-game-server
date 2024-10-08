package com.lzh.game.framework.common.method;

import com.lzh.game.framework.common.bean.BeanUtils;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024/6/21 21:58
 **/
public class MethodInvokeUtils {

    private final static Logger LOG = LoggerFactory.getLogger(MethodInvokeUtils.class);

    private static ClassPool pl;

    /**
     * Use Javassist to enhance Java method reflect
     *
     * @param bean
     * @param method
     * @return
     */
    public static MethodInvoke enhanceInvoke(Object bean, Method method) throws NotFoundException, CannotCompileException, NoSuchMethodException {

        String superName = bean.getClass().getName();
        String newClassName = superName.replaceAll("(.+)\\.(\\w+)", "$1.MethodInvoke$2") + "_" + method.getName();
        pl.appendClassPath(new ClassClassPath(bean.getClass()));

        CtClass enhance = pl.makeClass(newClassName);
        enhance.setInterfaces(new CtClass[]{pl.getCtClass(MethodInvoke.class.getName())});

        CtClass beanClass = pl.getCtClass(superName);
        LOG.debug("General invoke class name:{}", newClassName);
        CtField beanField = new CtField(beanClass, "bean", enhance);
        enhance.addField(beanField);

        CtConstructor constructor = new CtConstructor(new CtClass[]{beanClass}, enhance);
        constructor.setBody("{this.bean = $1;}");
        enhance.addConstructor(constructor);

        CtMethod invoke = CtNewMethod.make("public Object invoke(Object[] args) { " + buildInvokeMethodBody(method) + " }", enhance);
        enhance.addMethod(invoke);

//        enhance.getClassFile2().setMinorVersion(ClassFile.JAVA_8);
//        enhance.writeFile(MethodInvoke.class.getClassLoader().getResource("./").getFile());
        Constructor<?> enhanceConstructor = enhance.toClass().getConstructor(bean.getClass());
        return (MethodInvoke) BeanUtils.instantiateClass(enhanceConstructor, bean);
    }

    private static String buildInvokeMethodBody(Method method) {
        Class<?>[] types = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        String methodName = method.getName();
        if (returnType != Void.class && returnType != void.class) {
            builder.append("return ");
            addBeanInvokeToBody(builder, types, methodName);
        } else {
            addBeanInvokeToBody(builder, types, methodName);
            builder.append("return null;");
        }
        builder.append("}");
        return builder.toString();
    }

    private static void addBeanInvokeToBody(StringBuilder builder, Class<?>[] types, String methodName) {
        builder.append("this.bean.")
                .append(methodName)
                .append("(");
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            builder.append("(").append("(")
                    .append(BASE_CONVENT.getOrDefault(type, new String[]{type.getName(), ""})[0])
                    .append(")")
                    .append("args[").append(i).append("]").append(")");
            builder.append(BASE_CONVENT.getOrDefault(type, new String[]{"", ""})[1]);
            if (i != types.length - 1) {
                builder.append(",");
            }
        }
        builder.append(");");
    }

    private static final Map<Class<?>, String[]> BASE_CONVENT;

    static {
        pl = ClassPool.getDefault();
        pl.appendClassPath(new ClassClassPath(HandlerMethod.class));
        pl.importPackage("java.lang");
        BASE_CONVENT = new HashMap<>();
        // Autoboxing/Unboxing may be a big problem. avoid??
        BASE_CONVENT.put(Integer.TYPE, new String[]{"Integer", ".intValue()"});
        BASE_CONVENT.put(Double.TYPE, new String[]{"Double", ".doubleValue()"});
        BASE_CONVENT.put(Float.TYPE, new String[]{"Float", ".floatValue()"});
        BASE_CONVENT.put(Long.TYPE, new String[]{"Long", ".longValue()"});
        BASE_CONVENT.put(Boolean.TYPE, new String[]{"Boolean", ".booleanValue()"});
        BASE_CONVENT.put(Character.TYPE, new String[]{"Boolean", ".charValue()"});
    }
}
