package com.lzh.game.framework.socket.core.protocol.serial.impl;

import com.google.protobuf.CodedInputStream;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.exception.DecodeSerializeException;
import com.lzh.game.framework.socket.exception.EncodeSerializeException;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;

/**
 * @author zehong.l
 * @since 2024-06-25 11:41
 **/
public class MethodProtoSerialize implements MessageSerialize {

    @Override
    public Object decode(MessageDefine define, ByteBuf in) throws DecodeSerializeException {
        var type = define.getMsgClass();

//        CodedInputStream.newInstance()
        for (Field field : type.getFields()) {
            var fieldType = field.getType();
            if (!fieldType.isPrimitive()) {
                throw new DecodeSerializeException(define, define.getMsgId() + " params has not primitive type.");
            }

        }
        return null;
    }

    @Override
    public void encode(MessageDefine define, Object msg, ByteBuf out) throws EncodeSerializeException {

    }


}
