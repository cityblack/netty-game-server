package com.lzh.game.framework.socket.exception;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;

/**
 * @author zehong.l
 * @since 2024-06-18 18:25
 **/
public class EncodeSerializeException extends Exception {

    private MessageDefine define;

    public EncodeSerializeException(MessageDefine define, Exception exception) {
        super(exception);
        this.define = define;
    }

    public MessageDefine getDefine() {
        return define;
    }
}
