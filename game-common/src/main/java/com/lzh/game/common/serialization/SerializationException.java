package com.lzh.game.common.serialization;

import com.lzh.game.common.GameRuntimeException;

public class SerializationException extends GameRuntimeException {

    public static final int ERROR_CODE = 100;

    private static final long serialVersionUID = -4308536346316915612L;

    private static final String DEFAULT_MSG = "Nacos serialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "Nacos serialize for class [%s] failed. ";

    private Class<?> serializedClass;

    public SerializationException() {
        super(ERROR_CODE);
    }

    public SerializationException(Class<?> serializedClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()));
        this.serializedClass = serializedClass;
    }

    public SerializationException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public SerializationException(Class<?> serializedClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()), throwable);
        this.serializedClass = serializedClass;
    }

    public Class<?> getSerializedClass() {
        return serializedClass;
    }
}
