package com.lzh.game.socket.core.protocol.impl;

import com.lzh.game.socket.core.message.MessageDefine;
import com.lzh.game.socket.core.message.MessageManager;
import com.lzh.game.socket.core.protocol.MessageSerialize;
import io.netty.buffer.ByteBuf;

import java.util.Objects;

/**
 * @author zehong.l
 * @date 2024-04-07 14:54
 **/
public abstract class AbstractMessageSerialize implements MessageSerialize {

    private MessageManager manager;

    public AbstractMessageSerialize(MessageManager manager) {
        this.manager = manager;
    }

    @Override
    public Object decode(int msgId, ByteBuf in) throws Exception {
        MessageDefine define = manager.findDefine(msgId);
        if (Objects.isNull(define)) {
            throw new IllegalArgumentException("not defined [" + msgId + "] msg.");
        }
        return doDecode(define, in);
    }

    public abstract Object doDecode(MessageDefine define, ByteBuf in) throws Exception;
}
