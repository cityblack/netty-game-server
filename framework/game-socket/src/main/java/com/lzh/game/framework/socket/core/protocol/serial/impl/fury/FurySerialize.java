package com.lzh.game.framework.socket.core.protocol.serial.impl.fury;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.exception.DecodeSerializeException;
import com.lzh.game.framework.socket.exception.EncodeSerializeException;
import io.netty.buffer.ByteBuf;
import org.apache.fury.Fury;
import org.apache.fury.ThreadSafeFury;
import org.apache.fury.config.Language;

/**
 * @author zehong.l
 * @since 2024-07-02 15:56
 **/
public class FurySerialize implements MessageSerialize {

    private final ThreadSafeFury fury;

    public FurySerialize(MessageManager manager, FuryProperties properties) {
        var build = Fury.builder()
                .withRefTracking(true)
                .requireClassRegistration(true)
                .withAsyncCompilation(true);
        if (properties.isOnlyJava()) {
            build.withLanguage(Language.JAVA);
        }
        this.fury = build.buildThreadLocalFury();
        manager.addRegisterListen(e -> {
            var type = e.getMsgClass();
            if (type.isPrimitive() || type.isArray()) {
                return;
            }
            this.fury.register(type, e.getMsgId());
        });
    }

    @Override
    public Object decode(MessageDefine define, ByteBuf in) throws DecodeSerializeException {
        try {
            return fury.deserialize(in.nioBuffer());
        } catch (Exception e) {
            throw new DecodeSerializeException(define, e);
        }
    }

    @Override
    public void encode(MessageDefine define, Object msg, ByteBuf out) throws EncodeSerializeException {
        try {
            var bytes = fury.serialize(msg);
            out.writeBytes(bytes);
        } catch (Exception e) {
            throw new EncodeSerializeException(define, e);
        }
    }
}
