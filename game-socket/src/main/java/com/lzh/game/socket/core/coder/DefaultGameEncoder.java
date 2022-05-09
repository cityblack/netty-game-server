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
         * cmd: cmd int 4b
         * type: request / response byte 1b
         * request: int 4b
         * commandKey: process key / byte 1b
         * len: Object byte data length 4b
         * data: Object Serializable data
         */
        try {
            if (msg instanceof RemotingCommand) {
                RemotingCommand cmdMsg = (RemotingCommand) msg;
                int cmd = cmdMsg.cmd();
                out.writeInt(cmd);
                if (msg instanceof Response) {
                    out.writeByte(Constant.RESPONSE_SIGN);
                } else if (msg instanceof Request) {
                    out.writeByte(Constant.REQUEST_SIGN);
                }
                out.writeInt(cmdMsg.remoteId());
                int commandKey = cmdMsg.commandKey();
                out.writeByte(commandKey);
                Object data = cmdMsg.data();
                byte[] bytes = cmdMsg.byteData();
                if ((Objects.isNull(bytes) || bytes.length <= 0) && Objects.nonNull(data)) {
                    bytes = ProtoBufUtils.serialize(data);
                }
                if (Objects.nonNull(bytes) && bytes.length > 0) {
                    out.writeInt(bytes.length);
                    out.writeBytes(bytes);
                } else {
                    // len
                    out.writeInt(0);
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
}
