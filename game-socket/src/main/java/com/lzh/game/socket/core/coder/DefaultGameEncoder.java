package com.lzh.game.socket.core.coder;

import com.lzh.game.socket.RemotingCommand;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.Encoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.Objects;

public class DefaultGameEncoder implements Encoder {

    @Override
    public void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        /**
         * cmd: cmd int
         * type: request / response / oneWay byte
         * request: int
         * len: Object byte data length
         * data: Object Serializable data
         */
        try {
            if (msg instanceof RemotingCommand) {
                RemotingCommand cmdMsg = (RemotingCommand) msg;
                int cmd = cmdMsg.cmd();
                writeRawVarint32(out, cmd);
                out.writeByte(cmdMsg.type());
                writeRawVarint32(out, cmdMsg.remoteId());

                byte[] data = getDateBytes(cmdMsg);
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

    private static byte[] getDateBytes(RemotingCommand msg) {
        Object data = msg.data();
        byte[] bytes = msg.byteData();
        if ((Objects.isNull(bytes) || bytes.length <= 0) && Objects.nonNull(data)) {
            bytes = ProtoBufUtils.serialize(data);
        }
        return (Objects.isNull(bytes) || bytes.length <= 0) ? EMPTY_BYTES : bytes;
    }

    private final static byte[] EMPTY_BYTES = new byte[0];

    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        writeRawVarint64(byteBuf, 34L);
        System.out.println(byteBuf.array());
    }

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
