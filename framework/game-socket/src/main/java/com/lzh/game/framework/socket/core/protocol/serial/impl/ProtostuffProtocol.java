package com.lzh.game.framework.socket.core.protocol.serial.impl;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.exception.DecodeSerializeException;
import com.lzh.game.framework.socket.exception.EncodeSerializeException;
import com.lzh.game.framework.utils.ProtostuffUtils;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-04-07 14:53
 **/
public class ProtostuffProtocol implements MessageSerialize {

    @Override
    public Object decode(MessageDefine define, ByteBuf in) throws DecodeSerializeException {
        try {
            byte[] data = new byte[in.readableBytes()];
            return ProtostuffUtils.deSerialize(data, define.getMsgClass());
        } catch (Exception e) {
            throw new DecodeSerializeException(define, e);
        }
    }

    @Override
    public void encode(MessageDefine define, Object msg, ByteBuf out) throws EncodeSerializeException {
        try {
            byte[] data = ProtostuffUtils.serialize(msg);
            out.writeBytes(data);
        } catch (Exception e) {
            throw new EncodeSerializeException(define, e);
        }
    }
}
