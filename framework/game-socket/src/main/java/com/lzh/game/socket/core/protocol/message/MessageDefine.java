package com.lzh.game.socket.core.protocol.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zehong.l
 * @since 2024-04-07 14:22
 **/
@Data
@Accessors(chain = true)
public class MessageDefine {

    private int msgId;

    private Class<?> msgClass;

    private int protocolType;

    private int serializeType;

}
