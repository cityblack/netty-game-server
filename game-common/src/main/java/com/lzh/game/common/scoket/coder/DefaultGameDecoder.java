package com.lzh.game.common.scoket.coder;

import com.lzh.game.common.scoket.GameRequest;
import com.lzh.game.common.scoket.GameResponse;
import com.lzh.game.common.util.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class DefaultGameDecoder implements Decoder {

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * cmd: Request target
         * type: request / response
         * len: Object byte data length
         * data: Object Serializable data
         */
        if (in.readableBytes() < Constant.DEFAULT_HEAD_LEN) {
            return;
        }
        int cmd = in.readInt();
        byte type = in.readByte();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        if (type == Constant.REQUEST_SIGN) {
            GameRequest request = new GameRequest();
            request.setCmd(cmd);
            request.setBytes(length > 0 ? in.readBytes(length).array() : new byte[0]);
            out.add(request);
        } else if (type == Constant.RESPONSE_SIGN) {
            GameResponse response = new GameResponse();
            response.setCmd(cmd);
            response.setBytes(length > 0 ? in.readBytes(length).array() : new byte[0]);
            out.add(response);
        }
    }
}
