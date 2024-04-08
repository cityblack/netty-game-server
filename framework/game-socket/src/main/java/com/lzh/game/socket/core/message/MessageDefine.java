package com.lzh.game.socket.core.message;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zehong.l
 * @date 2024-04-07 14:22
 **/
@Data
@Accessors(chain = true)
public class MessageDefine {

    private int msgId;

    private Class<?> msgClass;

    private int protocolType;

    private int serializeType;

}