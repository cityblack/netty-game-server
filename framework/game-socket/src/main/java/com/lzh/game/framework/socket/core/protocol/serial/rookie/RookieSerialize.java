package com.lzh.game.framework.socket.core.protocol.serial.rookie;

import com.lzh.game.framework.rookie.Rookie;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.exception.DecodeSerializeException;
import com.lzh.game.framework.socket.exception.EncodeSerializeException;
import io.netty.buffer.ByteBuf;

/**
 * @author zehong.l
 * @since 2024-09-20 16:52
 **/
public class RookieSerialize implements MessageSerialize {

    private final Rookie rookie;

    public RookieSerialize(MessageManager manager) {
        this.rookie = new Rookie();
        manager.addRegisterListen("Rookie", this::registerMessage);
    }

    private void registerMessage(MessageDefine define) {
        rookie.register(define.getMsgId(), define.getMsgClass());
    }

    @Override
    public Object decode(MessageDefine define, ByteBuf in) throws DecodeSerializeException {
        return rookie.deserializer(in);
    }

    @Override
    public void encode(MessageDefine define, Object msg, ByteBuf out) throws EncodeSerializeException {
        rookie.serializer(out, msg);
    }
}
