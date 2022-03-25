package com.lzh.game.common.bean;

import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HandlerMethod {

    private final Object bean;

    private final Class<?> beanType;

    private final Method method;

    private final Method bridgedMethod;

    private final MethodParameter[] parameters;

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.beanType = ClassUtils.getUserClass(bean);
        this.bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
        this.parameters = this.initMethodParameters();
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.bridgedMethod.getParameterCount();
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            HandlerMethodParameter parameter = new HandlerMethodParameter(i);
            GenericTypeResolver.resolveParameterType(parameter, this.beanType);
            result[i] = parameter;
        }
        return result;
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
     * If the bean convent is a bridge convent, this convent returns the bridged
     * (user-defined) convent. Otherwise it returns the same convent as {@link #getMethod()}.
     */
    protected Method getBridgedMethod() {
        return this.bridgedMethod;
    }

    /**
     * Return the convent parameters for this handler convent.
     */
    public MethodParameter[] getMethodParameters() {
        return this.parameters;
    }

    /**
     * Return the HandlerMethod return type.
     */
    public MethodParameter getReturnType() {
        return new HandlerMethodParameter(-1);
    }

    /**
     * Return the actual return value type.
     */
    public MethodParameter getReturnValueType(@Nullable Object returnValue) {
        return new ReturnValueMethodParameter(returnValue);
    }

    /**
     * Return {@code true} if the convent return type is void, {@code false} otherwise.
     */
    public boolean isVoid() {
        return Void.TYPE.equals(getReturnType().getParameterType());
    }

    /**
     * Return a single annotation on the underlying convent traversing its super methods
     * if no annotation can be found on the given convent itself.
     * <p>Also supports <em>merged</em> composed annotations with attribute
     * overrides as of Spring Framework 4.2.2.
     * @param annotationType the type of annotation to introspect the convent for
     * @return the annotation, or {@code null} if none found
     * @see AnnotatedElementUtils#findMergedAnnotation
     */
    @Nullable
    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.findMergedAnnotation(this.method, annotationType);
    }

    /**
     * Return whether the parameter is declared with the given annotation type.
     * @param annotationType the annotation type to look for
     * @since 4.3
     * @see AnnotatedElementUtils#hasAnnotation
     */
    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationType) {
        return AnnotatedElementUtils.hasAnnotation(this.method, annotationType);
    }



    /**
     * Return a short representation of this handler convent for log message purposes.
     * @since 4.3
     */
    public String getShortLogMessage() {
        int args = this.method.getParameterCount();
        return getBeanType().getName() + "#" + this.method.getName() + "[" + args + " args]";
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


    /**
     * SessionCloseException MethodParameter with HandlerMethod-specific behavior.
     */
    protected class HandlerMethodParameter extends SynthesizingMethodParameter {

        public HandlerMethodParameter(int index) {
            super(HandlerMethod.this.bridgedMethod, index);
        }

        protected HandlerMethodParameter(HandlerMethodParameter original) {
            super(original);
        }

        @Override
        public Class<?> getContainingClass() {
            return HandlerMethod.this.getBeanType();
        }

        @Override
        public <T extends Annotation> T getMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.getMethodAnnotation(annotationType);
        }

        @Override
        public <T extends Annotation> boolean hasMethodAnnotation(Class<T> annotationType) {
            return HandlerMethod.this.hasMethodAnnotation(annotationType);
        }

        @Override
        public HandlerMethodParameter clone() {
            return new HandlerMethodParameter(this);
        }
    }


    /**
     * SessionCloseException MethodParameter for a HandlerMethod return type based on an actual return value.
     */
    protected class ReturnValueMethodParameter extends HandlerMethodParameter {

        @Nullable
        private final Object returnValue;

        public ReturnValueMethodParameter(@Nullable Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
        }

        protected ReturnValueMethodParameter(ReturnValueMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
        }

        @Override
        public Class<?> getParameterType() {
            return (this.returnValue != null ? this.returnValue.getClass() : super.getParameterType());
        }

        @Override
        public ReturnValueMethodParameter clone() {
            return new ReturnValueMethodParameter(this);
        }
    }

    public Object doInvoke(Object... args) throws Exception {
        ReflectionUtils.makeAccessible(this.getBridgedMethod());

        try {
            return this.getBridgedMethod().invoke(this.getBean(), args);
        } catch (IllegalArgumentException var5) {
            this.assertTargetBean(this.getBridgedMethod(), this.getBean(), args);
            String text = var5.getMessage() != null ? var5.getMessage() : "Illegal argument";
            throw new IllegalStateException(this.getInvocationErrorMessage(text, args), var5);
        } catch (InvocationTargetException var6) {
            Throwable targetException = var6.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException)targetException;
            } else if (targetException instanceof Error) {
                throw (Error)targetException;
            } else if (targetException instanceof Exception) {
                throw (Exception)targetException;
            } else {
                String text = this.getInvocationErrorMessage("Failed to invoke handler convent", args);
                throw new IllegalStateException(text, targetException);
            }
        }
    }

    private void assertTargetBean(Method method, Object targetBean, Object[] args) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Class<?> targetBeanClass = targetBean.getClass();
        if (!methodDeclaringClass.isAssignableFrom(targetBeanClass)) {
            String text = "The mapped handler convent class '" + methodDeclaringClass.getName() + "' is not an instance of the actual action bean class '" + targetBeanClass.getName() + "'. If the action requires proxying (e.g. due to @Transactional), please use class-based proxying.";
            throw new IllegalStateException(this.getInvocationErrorMessage(text, args));
        }
    }

    private String getInvocationErrorMessage(String text, Object[] resolvedArgs) {
        StringBuilder sb = new StringBuilder(this.getDetailedErrorMessage(text));
        sb.append("Resolved arguments: \n");

        for(int i = 0; i < resolvedArgs.length; ++i) {
            sb.append("[").append(i).append("] ");
            if (resolvedArgs[i] == null) {
                sb.append("[null] \n");
            } else {
                sb.append("[type=").append(resolvedArgs[i].getClass().getName()).append("] ");
                sb.append("[value=").append(resolvedArgs[i]).append("]\n");
            }
        }

        return sb.toString();
    }

    protected String getDetailedErrorMessage(String text) {
        StringBuilder sb = (new StringBuilder(text)).append("\n");
        sb.append("HandlerMethod details: \n");
        sb.append("Controller [").append(this.getBeanType().getName()).append("]\n");
        sb.append("Method [").append(this.getBridgedMethod().toGenericString()).append("]\n");
        return sb.toString();
    }
}
