package com.lzh.game.framework.socket.core.protocol.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zehong.l
 * @since 2024-04-07 14:22
 **/
@Data
@Accessors(chain = true)
public class MessageDefine {

    private short msgId;

    private Class<?> msgClass;

//    private int protocolType;

    private int serializeType;

    public MessageDefine(short msgId, Class<?> msgClass) {
        this.msgId = msgId;
        this.msgClass = msgClass;
    }

    public MessageDefine() {
    }
}
