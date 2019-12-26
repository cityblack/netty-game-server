package com.lzh.game.start.gm.service;

import com.lzh.game.common.bean.HandlerMethod;
import com.lzh.game.start.model.player.Player;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.stream.Stream;

public class GmHandlerMethod extends HandlerMethod {

    public GmHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    public Object invokeForValues(Player player, String[] values) throws Exception {
        Object[] argumentValues = this.getMethodArgumentValues(player,values);
        return doInvoke(argumentValues);
    }

    private Object[] getMethodArgumentValues(Player player, String[] values) throws Exception {
        MethodParameter[] parameters = this.getMethodParameters();
        Object[] args = new Object[parameters.length];

        if (values.length + 1 != parameters.length) {
            throw new RuntimeException("Gm request " + this.getMethod().getName()
            + " is lack of args or more args");
        }

        for(int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                args[i] = player; //the first argument must be Player
            } else {
                MethodParameter parameter = parameters[i];
                Class<?> type = parameter.getParameterType();
                if (isArray(type)) {
                    String[] array = new String[parameters.length - i];
                    System.arraycopy(values, i, array, 0, parameters.length - i);
                    args[i] = transformArray(type, array);
                    return values;
                }
                Object argValue = transformArgType(type, values[i - 1]);
                args[i] = argValue;
            }
        }

        return args;
    }

    /**
     * Transform gm request args type
     * Just support Integer Float Boolean Long
     * @param clazz
     * @param value
     * @return
     */
    private Object transformArgType(Class<?> clazz, String value) {

       if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
           return Integer.valueOf(value);
       } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
           return Float.valueOf(value);
       } else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz)) {
           return Boolean.valueOf(value);
       } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
           return Double.valueOf(value);
       } else if(Long.class.equals(clazz) || long.class.equals(clazz)) {
           return Long.valueOf(value);
       }

       return null;
    }

    private boolean isArray(Class<?> clazz) {
        return clazz.isArray();
    }

    private Object transformArray(Class<?> clazz, String[] value) {
        if (Integer[].class.equals(clazz) || int[].class.equals(clazz)) {
            return Stream.of(value).map(Integer::valueOf).toArray();
        } else if (Double[].class.equals(clazz) || double[].class.equals(clazz)) {
            return Stream.of(value).map(Double::valueOf).toArray();
        } else if (Float[].class.equals(clazz) || float[].class.equals(clazz)) {
            return Stream.of(value).map(Float::valueOf).toArray();
        } else if (Long[].class.equals(clazz) || long[].class.equals(clazz)) {
            return Stream.of(value).map(Long::valueOf).toArray();
        } else if (Boolean[].class.equals(clazz) || boolean[].class.equals(clazz)) {
            return Stream.of(value).map(Boolean::valueOf).toArray();
        }
        return null;
    }
}
