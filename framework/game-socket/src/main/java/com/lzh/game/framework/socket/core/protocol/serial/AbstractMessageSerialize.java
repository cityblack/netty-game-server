package com.lzh.game.framework.socket.core.protocol.serial;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-04-07 14:54
 **/
public abstract class AbstractMessageSerialize implements MessageSerialize {

    @Override
    public Object decode(MessageDefine define, ByteBuf in) throws Exception {

        return doDecode(define, in);
    }

    public abstract Object doDecode(MessageDefine define, ByteBuf in) throws Exception;
}
