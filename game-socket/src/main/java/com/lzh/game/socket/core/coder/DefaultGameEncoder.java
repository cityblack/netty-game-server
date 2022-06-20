package com.lzh.game.socket.core.coder;

import com.lzh.game.socket.RemotingCommand;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.Encoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.Objects;

public class DefaultGameEncoder implements Encoder {

    @Override
    public void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        /**
         * cmd: cmd int
         * type: request / response byte
         * request: int
         * commandKey: process key / byte
         * len: Object byte data length
         * data: Object Serializable data
         */
        try {
            if (msg instanceof RemotingCommand) {
                RemotingCommand cmdMsg = (RemotingCommand) msg;
                int cmd = cmdMsg.cmd();
                writeRawVarint32(out, cmd);

                if (msg instanceof Response) {
                    out.writeByte(Constant.RESPONSE_SIGN);
                } else if (msg instanceof Request) {
                    out.writeByte(Constant.REQUEST_SIGN);
                }
                writeRawVarint32(out, cmdMsg.remoteId());

                int commandKey = cmdMsg.commandKey();
                writeRawVarint32(out, commandKey);

                Object data = cmdMsg.data();
                byte[] bytes = cmdMsg.byteData();
                if ((Objects.isNull(bytes) || bytes.length <= 0) && Objects.nonNull(data)) {
                    bytes = ProtoBufUtils.serialize(data);
                }
                if (Objects.nonNull(bytes) && bytes.length > 0) {
                    writeRawVarint32(out, bytes.length);
                    out.writeBytes(bytes);
                } else {
                    // len
                    writeRawVarint32(out, 0);
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
}
