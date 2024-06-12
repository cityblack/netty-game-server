package com.lzh.game.socket.core.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zehong.l
 * @since 2024-04-07 15:08
 **/
@Getter
@Setter
public class AbstractCommand {

    private byte type;

    private int msgId;

    private int requestId;

    private Object data;

    private Class<?> dataClass;

}
