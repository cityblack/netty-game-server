package com.lzh.game.framework.socket.core.protocol.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Constructor;

/**
 * @author zehong.l
 * @since 2024-04-07 14:22
 **/
@Data
@Accessors(chain = true)
public class MessageDefine {

    private short msgId;

    private Class<?> msgClass;

    private int serializeType;

    private Class<?>[] fieldTypes;

    private Constructor<?> allArgsConstructor;

    private boolean compose;

    public MessageDefine() {
    }
}
