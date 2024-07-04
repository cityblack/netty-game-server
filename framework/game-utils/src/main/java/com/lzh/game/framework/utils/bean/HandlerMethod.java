package com.lzh.game.framework.utils.bean;

import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author zehong.l
 * @since 2024/6/21 21:57
 **/
public class HandlerMethod implements MethodInvoke {

    private final Object bean;

    private final Class<?> beanType;

    private final Method method;

    private final Class<?>[] paramsType;

    private final Class<?> returnType;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.beanType = ClassUtils.getUserClass(bean);
        this.paramsType = method.getParameterTypes();
        this.returnType = method.getReturnType();
    }

    public Class<?>[] getParamsType() {
        return paramsType;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    /**
     * Return the bean for this handler convent.
     */
    public Object getBean() {
        return this.bean;
    }

    /**
     * Return the convent for this handler convent.
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * This convent returns the type of the handler for this handler convent.
     * <p>Note that if the bean type is a CGLIB-generated class, the original
     * user-defined class is returned.
     */
    public Class<?> getBeanType() {
        return this.beanType;
    }



    /**
     * Return the actual return value type.
     */
    /**
     * Return {@code true} if the convent return type is void, {@code false} otherwise.
     */
    public boolean isVoid() {
        return Void.TYPE.equals(getReturnType());
    }

    public String getShortLogMessage() {
        int args = this.method.getParameterCount();
        return getBeanType().getName() + "#" + this.method.getName() + "[" + args + " args]";
    }

    public Annotation[][] getParameterAnnotations() {
        return method.getParameterAnnotations();
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof HandlerMethod)) {
            return false;
        }
        HandlerMethod otherMethod = (HandlerMethod) other;
        return (this.bean.equals(otherMethod.bean) && this.method.equals(otherMethod.method));
    }

    @Override
    public int hashCode() {
        return (this.bean.hashCode() * 31 + this.method.hashCode());
    }

    @Override
    public String toString() {
        return this.method.toGenericString();
    }

    public String getName() {
        return method.getName();
    }

    @Override
    public Object invoke(Object... args) {
        try {
            return this.getMethod().invoke(bean, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
