package com.lzh.game.framework.common.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-09-25 17:12
 **/
public class BeanUtils {

    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES = Map.of(
            boolean.class, false,
            byte.class, (byte) 0,
            short.class, (short) 0,
            int.class, 0,
            long.class, 0L,
            float.class, 0F,
            double.class, 0D,
            char.class, '\0');

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws IllegalArgumentException {
        if (Objects.isNull(ctor)) {
            throw new IllegalArgumentException("Constructor must not be null");
        }
        try {
            ctor.setAccessible(true);
            int parameterCount = ctor.getParameterCount();
            if (args.length > parameterCount) {
                throw new IllegalArgumentException("Can't specify more arguments than constructor parameters");
            }
            if (parameterCount == 0) {
                return ctor.newInstance();
            }
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            Object[] argsWithDefaultValues = new Object[args.length];
            for (int i = 0 ; i < args.length; i++) {
                if (args[i] == null) {
                    Class<?> parameterType = parameterTypes[i];
                    argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
                }
                else {
                    argsWithDefaultValues[i] = args[i];
                }
            }
            return ctor.newInstance(argsWithDefaultValues);
        }
        catch (InstantiationException ex) {
            throw new IllegalArgumentException("ctor Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("ctor Is the constructor accessible?", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Illegal arguments for constructor", ex);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalArgumentException("Constructor threw exception", ex.getTargetException());
        }
    }

}
