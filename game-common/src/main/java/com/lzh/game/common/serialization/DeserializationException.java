package com.lzh.game.common.serialization;

import com.lzh.game.common.GameRuntimeException;

import java.lang.reflect.Type;

public class DeserializationException extends GameRuntimeException {

    public static final int ERROR_CODE = 101;

    private static final long serialVersionUID = -2742350751684273728L;

    private static final String DEFAULT_MSG = "Deserialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "Deserialize for class [%s] failed. ";

    private Class<?> targetClass;

    public DeserializationException() {
        super(ERROR_CODE);
    }

    public DeserializationException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public DeserializationException(Type targetType) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()));
    }

    public DeserializationException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public DeserializationException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public DeserializationException(Type targetType, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()), throwable);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
