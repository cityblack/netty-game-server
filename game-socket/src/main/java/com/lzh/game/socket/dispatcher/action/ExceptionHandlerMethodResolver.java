package com.lzh.game.socket.dispatcher.action;

import com.lzh.game.socket.annotation.ExceptionHandler;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

public class ExceptionHandlerMethodResolver {

    /**
     * A filter for selecting {@code @ExceptionHandler} methods.
     */
    public static final ReflectionUtils.MethodFilter EXCEPTION_HANDLER_METHODS = method ->
            (AnnotationUtils.findAnnotation(method, ExceptionHandler.class) != null);


    private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap<>(16);

    private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap<>(16);


    /**
     * A constructor that finds {@link ExceptionHandler} methods in the given type.
     * @param handlerType the type to introspect
     */
    public ExceptionHandlerMethodResolver(Class<?> handlerType) {
        for (Method method : MethodIntrospector.selectMethods(handlerType, EXCEPTION_HANDLER_METHODS)) {
            for (Class<? extends Throwable> exceptionType : detectExceptionMappings(method)) {
                addExceptionMapping(exceptionType, method);
            }
        }
    }


    /**
     * Extract exception mappings from the {@code @ExceptionHandler} annotation first,
     * and then as a fallback from the convent signature itself.
     */
    @SuppressWarnings("unchecked")
    private List<Class<? extends Throwable>> detectExceptionMappings(Method method) {
        List<Class<? extends Throwable>> result = new ArrayList<>();
        detectAnnotationExceptionMappings(method, result);
        if (result.isEmpty()) {
            for (Class<?> paramType : method.getParameterTypes()) {
                if (Throwable.class.isAssignableFrom(paramType)) {
                    result.add((Class<? extends Throwable>) paramType);
                }
            }
        }
        if (result.isEmpty()) {
            throw new IllegalStateException("No exception types mapped to " + method);
        }
        return result;
    }

    protected void detectAnnotationExceptionMappings(Method method, List<Class<? extends Throwable>> result) {
        ExceptionHandler ann = AnnotationUtils.findAnnotation(method, ExceptionHandler.class);
        Assert.state(ann != null, "No ExceptionHandler annotation");
        result.addAll(Arrays.asList(ann.value()));
    }

    private void addExceptionMapping(Class<? extends Throwable> exceptionType, Method method) {
        Method oldMethod = this.mappedMethods.put(exceptionType, method);
        if (oldMethod != null && !oldMethod.equals(method)) {
            throw new IllegalStateException("Ambiguous @ExceptionHandler convent mapped for [" +
                    exceptionType + "]: {" + oldMethod + ", " + method + "}");
        }
    }

    /**
     * Whether the contained type has any exception mappings.
     */
    public boolean hasExceptionMappings() {
        return !this.mappedMethods.isEmpty();
    }

    /**
     * Find a {@link Method} to handler the given exception.
     * Use {@link ExceptionDepthComparator} if more than one match is found.
     * @param exception the exception
     * @return a Method to handler the exception, or {@code null} if none found
     */
    @Nullable
    public Method resolveMethod(Exception exception) {
        return resolveMethodByThrowable(exception);
    }

    /**
     * Find a {@link Method} to handler the given Throwable.
     * Use {@link ExceptionDepthComparator} if more than one match is found.
     * @param exception the exception
     * @return a Method to handler the exception, or {@code null} if none found
     * @since 5.0
     */
    @Nullable
    public Method resolveMethodByThrowable(Throwable exception) {
        Method method = resolveMethodByExceptionType(exception.getClass());
        if (method == null) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                method = resolveMethodByExceptionType(cause.getClass());
            }
        }
        return method;
    }

    /**
     * Find a {@link Method} to handler the given exception type. This can be
     * useful if an {@link Exception} instance is not available (e.g. for tools).
     * @param exceptionType the exception type
     * @return a Method to handler the exception, or {@code null} if none found
     */
    @Nullable
    public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
        Method method = this.exceptionLookupCache.get(exceptionType);
        if (method == null) {
            method = getMappedMethod(exceptionType);
            this.exceptionLookupCache.put(exceptionType, method);
        }
        return method;
    }

    /**
     * Return the {@link Method} mapped to the given exception type, or {@code null} if none.
     */
    @Nullable
    private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
        List<Class<? extends Throwable>> matches = new ArrayList<>();
        for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
            if (mappedException.isAssignableFrom(exceptionType)) {
                matches.add(mappedException);
            }
        }
        if (!matches.isEmpty()) {
            matches.sort(new ExceptionDepthComparator(exceptionType));
            return this.mappedMethods.get(matches.get(0));
        }
        else {
            return null;
        }
    }

}
