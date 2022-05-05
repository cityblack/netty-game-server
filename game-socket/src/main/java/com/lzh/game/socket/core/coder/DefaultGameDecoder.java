package com.lzh.game.socket.core.coder;

import com.lzh.game.socket.AbstractRemotingCommand;
import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.GameResponse;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.Decoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Objects;

public class DefaultGameDecoder implements Decoder {

    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }
        /**
         * cmd: cmd int 4b
         * type: request / response byte 1b
         * request: int 4b
         * commandKey: process key / byte 1b
         * len: Object byte data length 4b
         * data: Object Serializable data
         */
        if (in.readableBytes() < Constant.DEFAULT_HEAD_LEN) {
            return;
        }
        int cmd = in.readInt();
        byte type = in.readByte();
        int requestId = in.readInt();
        byte commandKey = in.readByte();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        AbstractRemotingCommand remotingCmd = null;
        if (type == Constant.REQUEST_SIGN) {
            remotingCmd = new GameRequest();
        } else if (type == Constant.RESPONSE_SIGN) {
            remotingCmd = new GameResponse();
        }
        if (Objects.isNull(remotingCmd)) {
            in.resetReaderIndex();
            throw new IllegalArgumentException("Not defined cmd type:" + type);
        }
        remotingCmd.setCmd(cmd);
        remotingCmd.setRemoteId(requestId);
        remotingCmd.setCommonKey(commandKey);
        if (length > 0) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            remotingCmd.setBytes(bytes);
        }
        in.markReaderIndex();
        out.add(remotingCmd);
    }
}
