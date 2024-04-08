package com.lzh.game.socket.core.protocol.codec;

import com.lzh.game.socket.Constant;
import com.lzh.game.socket.Request;
import com.lzh.game.socket.Response;
import com.lzh.game.socket.core.AbstractRemotingCommand;
import com.lzh.game.socket.core.Decoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;
import java.util.Objects;

/**
 * @author zehong.l
 * @date 2024-04-07 16:20
 **/
public abstract class RemoteMessageDecoder implements Decoder {

    @Override
    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        /**
         * len: int
         * msgId: msg int
         * type: request / response / one way byte
         * request: int
         * data: Object Serializable data
         */
        if (in.readableBytes() < Constant.HEAD_MIN_LEN) {
            return;
        }
        in.markReaderIndex();
        int len = readRawVarint32(in);
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }
        int startIndex = in.readerIndex();
        byte type = in.readByte();
        int msgId = readRawVarint32(in);
        int requestId = readRawVarint32(in);
        int dataLen = len - in.readableBytes() + startIndex;

        Object o = decode(channelHandlerContext, in, msgId, dataLen);
        if (Objects.isNull(o)) {
            return;
        }
        AbstractRemotingCommand command = Constant.isRequest(type) ?
                Request.of(msgId, requestId, o) : Response.of(msgId, requestId, o);
        command.setType(type);
        list.add(command);
    }

    protected static int readRawVarint32(ByteBuf buffer) {
        if (!buffer.isReadable()) {
            return 0;
        } else {
            buffer.markReaderIndex();
            byte tmp = buffer.readByte();
            if (tmp >= 0) {
                return tmp;
            } else {
                int result = tmp & 127;
                if (!buffer.isReadable()) {
                    buffer.resetReaderIndex();
                    return 0;
                } else {
                    if ((tmp = buffer.readByte()) >= 0) {
                        result |= tmp << 7;
                    } else {
                        result |= (tmp & 127) << 7;
                        if (!buffer.isReadable()) {
                            buffer.resetReaderIndex();
                            return 0;
                        }

                        if ((tmp = buffer.readByte()) >= 0) {
                            result |= tmp << 14;
                        } else {
                            result |= (tmp & 127) << 14;
                            if (!buffer.isReadable()) {
                                buffer.resetReaderIndex();
                                return 0;
                            }

                            if ((tmp = buffer.readByte()) >= 0) {
                                result |= tmp << 21;
                            } else {
                                result |= (tmp & 127) << 21;
                                if (!buffer.isReadable()) {
                                    buffer.resetReaderIndex();
                                    return 0;
                                }

                                result |= (tmp = buffer.readByte()) << 28;
                                if (tmp < 0) {
                                    throw new CorruptedFrameException("malformed varint.");
                                }
                            }
                        }
                    }

                    return result;
                }
            }
        }
    }

    public abstract Object decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, int msg, int dataLen) throws Exception;
}
