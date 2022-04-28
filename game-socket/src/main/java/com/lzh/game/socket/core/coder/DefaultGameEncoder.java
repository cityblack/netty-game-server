package com.lzh.game.socket.core.coder;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.common.util.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;
import java.util.Objects;

public class DefaultGameEncoder implements Encoder {

    @Override
    public void encode(ChannelHandlerContext ctx, Serializable msg, ByteBuf out) throws Exception {
        /**
         * cmd: Request target
         * type: request / response
         * len: Object byte data length
         * data: Object Serializable data
         */
        if (msg instanceof RemotingCmd) {
            RemotingCmd cmdMsg = (RemotingCmd) msg;
            int cmd = cmdMsg.cmd();
            out.writeInt(cmd);
            if (msg instanceof Response) {
                out.writeByte(Constant.RESPONSE_SIGN);
            } else if (msg instanceof Request) {
                out.writeByte(Constant.REQUEST_SIGN);
            }
            Object data = cmdMsg.data();
            if (Objects.isNull(data)) {
                out.writeInt(0);
            } else {
                byte[] bytes = ProtoBufUtils.serialize(data);
                out.writeInt(bytes.length);
                out.writeBytes(bytes);
            }
        } else if (msg instanceof ByteBuf) {
            out.writeBytes((ByteBuf) msg);
        } else if (msg instanceof byte[]) {
            out.writeBytes((byte[]) msg);
        }
    }
}
