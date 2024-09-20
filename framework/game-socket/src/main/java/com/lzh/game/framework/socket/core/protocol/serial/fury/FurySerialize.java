package com.lzh.game.framework.socket.core.protocol.serial.fury;

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
 * msg id limit. 0 ~ Short.MAX
 * not support c#, lua
 * @author zehong.l
 * @since 2024-07-02 15:56
 **/
public class FurySerialize implements MessageSerialize {

    private ThreadSafeFury fury;

    public FurySerialize(FuryProperties properties) {
        this.iniFury(properties);
    }

    public FurySerialize(MessageManager manager, FuryProperties properties) {
        this.iniFury(properties);
        manager.addRegisterListen("Fury", e -> {
            FurySerialize.this.fury.register(e.getMsgClass(), e.getMsgId(), true);
        });
    }

    private void iniFury(FuryProperties properties) {
        var build = Fury.builder()
                .withRefTracking(true)
                .requireClassRegistration(true)
                .withAsyncCompilation(true);
        if (properties.isOnlyJava()) {
            build.withLanguage(Language.JAVA);
        }
        this.fury = build.buildThreadLocalFury();
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
