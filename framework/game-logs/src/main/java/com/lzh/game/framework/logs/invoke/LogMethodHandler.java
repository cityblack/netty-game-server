package com.lzh.game.framework.logs.invoke;

import com.lzh.game.framework.logs.desc.LogDescHandler;
import javassist.util.proxy.MethodHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-07-12 11:06
 **/
@Slf4j
public class LogMethodHandler implements MethodHandler {

    private final Map<String, MethodInvoke> methods = new HashMap<>();

    public LogMethodHandler(LogInvoke logInvoke, Class<?> clz, LogDescHandler descHandler) {
        for (Method method : clz.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())
                    || !Modifier.isAbstract(method.getModifiers()) || method.isDefault()) {
                continue;
            }
            if (method.getReturnType() != void.class) {
                throw new RuntimeException("The method " + clz.getName() + "#" + method.getName() + "is not void.");
            }
//            T anno = method.getAnnotation(descHandler.descAnno());
            var desc = descHandler.getDefined(method);
            var key = method.getName();
            if (Objects.isNull(desc)) {
                throw new RuntimeException("The method " + clz.getName() + "#" + key + " not defined desc. ");
            }
            if (methods.containsKey(key)) {
                throw new RuntimeException("Duplicate definition method:" + clz.getName() + "#" + key);
            }
            methods.put(method.getName(), new MethodInvoke(logInvoke, clz, method, desc));
        }
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        try {
            var name = thisMethod.getName();
            var invoke = methods.get(name);
            Assert.notNull(invoke, "Can not find " + name);
            invoke.doLog(args);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }
}
