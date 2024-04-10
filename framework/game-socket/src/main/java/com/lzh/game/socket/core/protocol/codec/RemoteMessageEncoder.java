package com.lzh.game.socket.core.protocol.codec;

import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.core.protocol.AbstractCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.Objects;

public abstract class RemoteMessageEncoder implements Encoder {

    @Override
    public void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        /**
         * len: int
         * msgId: msg int
         * type: request / response / one way byte
         * request: int
         * data: Object Serializable data
         */
        try {

            if (msg instanceof AbstractCommand command) {
                int msgId = command.getMsgId();
                writeRawVarint32(out, msgId);
                out.writeByte(command.getType());
                writeRawVarint32(out, command.getRequestId());
                byte[] data = getDateBytes(command);
                int len = data.length;
                writeRawVarint32(out, len);
                if (data.length > 0) {
                    out.writeBytes(data);
                }
            } else if (msg instanceof ByteBuf) {
                out.writeBytes((ByteBuf) msg);
            } else if (msg instanceof byte[]) {
                out.writeBytes((byte[]) msg);
            }
            out.markWriterIndex();
        } catch (Exception e) {
            out.resetWriterIndex();
        }
    }

    static void writeRawVarint32(ByteBuf out, int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.writeByte(value);
                return;
            } else {
                out.writeByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    private static byte[] getDateBytes(AbstractCommand msg) {
        Object data = msg.getData();
//        byte[] bytes = msg.byteData();
        if ((Objects.isNull(bytes) || bytes.length == 0) && Objects.nonNull(data)) {
            bytes = ProtoBufUtils.serialize(data);
        }
        return (Objects.isNull(bytes) || bytes.length == 0) ? EMPTY_BYTES : bytes;
    }

    private final static byte[] EMPTY_BYTES = new byte[0];

    static void writeRawVarint64(ByteBuf out, long value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.writeByte((int) value);
                return;
            } else {
                out.writeByte((int) ((value & 0x7F) | 0x80));
                value >>>= 7;
            }
        }
    }
}
