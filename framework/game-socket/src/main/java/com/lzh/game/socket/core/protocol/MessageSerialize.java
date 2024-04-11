package com.lzh.game.socket.core.protocol;

import com.lzh.game.socket.core.message.NetMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @date 2024-04-07 14:44
 **/
public interface MessageSerialize {

    Object decode(int msgId, ByteBuf in) throws Exception;

    void encode(int msgId, Object msg, ByteBuf out) throws Exception;
}
