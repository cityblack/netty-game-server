package com.lzh.game.common.bean;

import javassist.*;
import javassist.bytecode.ClassFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class MethodInvokeUtils {

    private static ClassPool pl;

    /**
     * Use Javassist to enhance Java method reflect
     *
     * @param bean
     * @param method
     * @return
     */
    public static MethodInvoke enhanceInvoke(Object bean, Method method) throws NotFoundException, CannotCompileException, IOException, NoSuchMethodException {

        String superName = bean.getClass().getName();
        String newClassName = superName.replaceAll("(.+)\\.(\\w+)", "$1.MethodInvoke$2") + "_" + method.getName();
        pl.appendClassPath(new ClassClassPath(bean.getClass()));

        CtClass enhance = pl.makeClass(newClassName);
        enhance.setInterfaces(new CtClass[]{pl.getCtClass(MethodInvoke.class.getName())});

        CtClass beanClass = pl.getCtClass(superName);
        log.info("General invoke class name:{}", newClassName);
        CtField beanField = new CtField(beanClass, "bean", enhance);
        enhance.addField(beanField);

        CtConstructor constructor = new CtConstructor(new CtClass[]{beanClass}, enhance);
        constructor.setBody("{this.bean = $1;}");
        enhance.addConstructor(constructor);

        CtMethod invoke = CtNewMethod.make("public Object invoke(Object[] args) { " + buildInvokeMethodBody(method) + " }", enhance);
        enhance.addMethod(invoke);

        enhance.getClassFile2().setMinorVersion(ClassFile.JAVA_8);
        enhance.writeFile(MethodInvoke.class.getClassLoader().getResource("./").getFile());
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
        builder.append("this.bean.").append(methodName).append("(");
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            checkParamType(type);
            builder.append("(")
                    .append(type.getName())
                    .append(")")
                    .append("args[").append(i).append("]");
            if (i != types.length - 1) {
                builder.append(",");
            }
        }
        builder.append(");");
    }

    // no support base type
    private static void checkParamType(Class<?> clz) {
        if (clz == int.class || clz == short.class || clz == boolean.class ||
                clz == float.class || clz == double.class || clz == char.class) {
            throw new IllegalArgumentException("Enhance method params no support base type. Please change the base type to Object. like int convert to Integer.");
        }
    }

    static {
        pl = ClassPool.getDefault();
        pl.appendClassPath(new ClassClassPath(HandlerMethod.class));
        pl.importPackage("java.lang");
    }

    public static void main(String[] args) throws NoSuchMethodException, IOException, CannotCompileException, NotFoundException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        class A {
            public String s(String s) {
                System.out.println(s);
                return "world";
            }

            public String hello(Integer i) {
                System.out.println(i);
                return "hello world";
            }
        }

        A a = new A();
        MethodInvoke invoke = MethodInvokeUtils.enhanceInvoke(a, a.getClass().getMethod("hello", Integer.class));
        System.out.println(invoke.invoke(123));

        MethodInvoke s = MethodInvokeUtils.enhanceInvoke(a, a.getClass().getMethod("s", String.class));
        System.out.println(s.invoke("hello"));
    }


}
