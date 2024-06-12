package com.lzh.game.business.exception;

import java.lang.reflect.Type;

public class SerializationException extends GameRuntimeException {

    public static final int ERROR_CODE = 101;

    private static final long serialVersionUID = -2742350751684273728L;

    private static final String DEFAULT_MSG = "Deserialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "Deserialize for class [%s] failed. ";

    private Class<?> targetClass;

    public SerializationException() {
        super(ERROR_CODE);
    }

    public SerializationException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public SerializationException(Type targetType) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()));
    }

    public SerializationException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public SerializationException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public SerializationException(Type targetType, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()), throwable);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
