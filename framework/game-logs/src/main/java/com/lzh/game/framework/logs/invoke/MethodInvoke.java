package com.lzh.game.framework.logs.invoke;

import com.lzh.game.framework.logs.desc.LogDescDefined;
import com.lzh.game.framework.logs.param.LogReasonParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

/**
 * @author zehong.l
 * @since 2024-07-12 11:12
 **/
public class MethodInvoke {

    private final LogInvokeInfo invokeInfo;

    private final LogInvoke invoke;

    public MethodInvoke(LogInvoke invoke, Class<?> from, Method sourceMethod, LogDescDefined descDefined) {
        this.invoke = invoke;
        Parameter[] parameters = sourceMethod.getParameters();
        var names = new String[parameters.length];
        var types = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];
            names[i] = parameter.getName();
            types[i] = parameter.getType();
        }
        int reasonIndex = checkAndGetReasonIndex(sourceMethod, types, descDefined, from);
        this.invokeInfo = new LogInvokeInfo()
                .setDescDefined(descDefined)
                .setParamNames(names)
                .setParamTypes(types)
                .setParentClass(from)
                .setParamLogReasonIndex(reasonIndex);
    }

    private int checkAndGetReasonIndex(Method method, Class<?>[] types, LogDescDefined descDefined, Class<?> clz) {
        var count = Stream.of(types).filter(LogReasonParam.class::isAssignableFrom).count();
        if (count > 1) {
            throw new RuntimeException(clz.getName() + "#" + method.getName() + "'s LogReasonDefined param not unique.");
        }
        var index = findReasonIndex(types);
        if (index == -1 && descDefined.getLogReason() == 0) {
            throw new RuntimeException();
        }
        if (index >= 0 && descDefined.getLogReason() != 0) {
            throw new RuntimeException();
        }
        return index;
    }

    private int findReasonIndex(Class<?>[] types) {
        for (int i = 0; i < types.length; i++) {
            var type = types[i];
            if (LogReasonParam.class.isAssignableFrom(type)) {
                return i;
            }
        }
        return -1;
    }

    public void doLog(Object[] args) {
        invoke.invoke(this.invokeInfo, args);
    }

}
