package com.lzh.netty.framework.gm;

import com.lzh.netty.socket.method.HandlerMethod;
import com.lzh.netty.framework.core.player.Player;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class GmHandlerMethod extends HandlerMethod {

    public GmHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    public Object invokeForValues(Player player, String[] values) throws Exception {
        Object[] argumentValues = this.getMethodArgumentValues(player,values);
        return doInvoke(argumentValues);
    }

    private Object doInvoke(Object[] values) {
        ReflectionUtils.makeAccessible(this.getBridgedMethod());

        try {
            return this.getBridgedMethod().invoke(getBean(),values);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
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
                Object argValue = transformArgType(parameter.getParameterType(),values[i - 1]);
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

}
