package com.lzh.game.framework.socket.exception;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;

/**
 * @author zehong.l
 * @since 2024-06-18 18:28
 **/
public class DecodeSerializeException extends Exception {

    private MessageDefine define;

    public DecodeSerializeException(MessageDefine define, Exception exception) {
        super(exception);
        this.define = define;
    }

    public DecodeSerializeException(MessageDefine define, String message) {
        super(message);
        this.define = define;
    }

    public MessageDefine getDefine() {
        return define;
    }

}
