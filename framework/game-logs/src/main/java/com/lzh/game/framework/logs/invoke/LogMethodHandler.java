package com.lzh.game.framework.logs.invoke;

import com.lzh.game.framework.logs.desc.LogDescHandler;
import javassist.util.proxy.MethodHandler;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-07-12 11:06
 **/
public class LogMethodHandler implements MethodHandler {

    private final Map<String, MethodInvoke> methods = new HashMap<>();

    public <T extends Annotation> LogMethodHandler(LogInvoke logInvoke, Class<?> clz, LogDescHandler<T> descHandler) {
        for (Method method : clz.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())
                    || Modifier.isAbstract(method.getModifiers()) || method.isDefault()) {
                continue;
            }
            if (method.getReturnType() != Void.class) {
                throw new RuntimeException("The method " + clz.getName() + "#" + method.getName() + "is not void.");
            }
            T anno = method.getAnnotation(descHandler.descAnno());
            var key = method.getName();
            if (Objects.isNull(anno)) {
                throw new RuntimeException("The method " + clz.getName() + "#" + key + " not has @" + anno.getClass().getName());
            }
            if (methods.containsKey(key)) {
                throw new RuntimeException("Duplicate definition method:" + clz.getName() + "#" + key);
            }
            methods.put(method.getName(), new MethodInvoke(logInvoke, clz, method, descHandler.getDefined(anno)));
        }
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        var name = thisMethod.getName();
        var invoke = methods.get(name);
        Assert.notNull(invoke, "Can not find " + name);
        invoke.doLog(args);
        return null;
    }
}
