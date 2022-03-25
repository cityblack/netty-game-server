package com.lzh.game.common.bean;

import javassist.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@Slf4j
public class MethodInvokeUtils {

    private static ClassPool pl;

    private static String OBJECT_ARR_STR = "([Ljava/lang/Object;)Ljava/lang/Object;";

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

        CtClass enhance = pl.makeClass(newClassName);
        enhance.setInterfaces(new CtClass[]{pl.getCtClass(MethodInvoke.class.getName())});

        CtClass beanClass = pl.getCtClass(superName);
        log.info("General invoke class name:{}", newClassName);
        CtField beanField = new CtField(beanClass, "bean", enhance);
        enhance.addField(beanField);

        CtConstructor constructor = new CtConstructor(new CtClass[]{beanClass}, enhance);
        constructor.setBody("{this.bean = $1;}");
        enhance.addConstructor(constructor);

        String methodName = "invoke";
        CtClass methodClass = pl.getCtClass(MethodInvoke.class.getName());
        CtMethod ctMethod = methodClass.getMethod(methodName, OBJECT_ARR_STR);
        CtMethod invoke = new CtMethod(ctMethod.getReturnType(), methodName, ctMethod.getParameterTypes(), enhance);
        String methodBody = buildInvokeMethodBody(method);
        invoke.setBody(methodBody);
        enhance.addMethod(invoke);
        Constructor<?> enhanceConstructor = enhance.toClass().getConstructor(bean.getClass());
        return (MethodInvoke) BeanUtils.instantiateClass(enhanceConstructor, bean);
    }

    private static String buildInvokeMethodBody(Method method) {
        Class<?>[] types = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if (returnType != Void.class) {
            builder.append("return ");
        }
        builder.append("this.bean.").append(method.getName()).append("(");
        for (int i = 0; i < types.length; i++) {
            Class<?> type = types[i];
            builder.append("(").append(type.getName()).append(")")
                    .append("$1[").append(i).append("]");
            if (i != types.length - 1) {
                builder.append(",");
            }
        }
        builder.append(");}");
        return builder.toString();
    }

    static {
        pl = ClassPool.getDefault();
        pl.appendClassPath(new ClassClassPath(HandlerMethod.class));
        pl.importPackage("java.lang");
    }

    public static void main(String[] args) throws NoSuchMethodException, IOException, CannotCompileException, NotFoundException {
        A a = new A();
        MethodInvoke invoke = MethodInvokeUtils.enhanceInvoke(a, a.getClass().getMethod("s", String.class));
        System.out.println(invoke.invoke("ssss"));
    }

    static class A {
        public String s(String s) {
            System.out.println(s);
            return "hello world";
        }

        ;
    }
}
