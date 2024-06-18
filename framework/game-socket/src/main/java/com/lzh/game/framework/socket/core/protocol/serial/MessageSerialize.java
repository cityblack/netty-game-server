package com.lzh.game.framework.socket.core.protocol.serial;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.exception.DecodeSerializeException;
import com.lzh.game.framework.socket.exception.EncodeSerializeException;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-04-07 14:44
 **/
public interface MessageSerialize {

    Object decode(MessageDefine define, ByteBuf in) throws DecodeSerializeException;

    void encode(MessageDefine define, Object msg, ByteBuf out) throws EncodeSerializeException;
}
