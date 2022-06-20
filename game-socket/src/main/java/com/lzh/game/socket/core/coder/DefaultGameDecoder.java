package com.lzh.game.socket.core.coder;

import com.lzh.game.socket.AbstractRemotingCommand;
import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.GameResponse;
import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.core.Decoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;
import java.util.Objects;

public class DefaultGameDecoder implements Decoder {

    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }
        /**
         * cmd: cmd int
         * type: request / response byte
         * request: int
         * commandKey: process key / byte
         * len: Object byte data length
         * data: Object Serializable data
         */
        if (in.readableBytes() < Constant.DEFAULT_HEAD_LEN) {
            return;
        }
        int cmd = readRawVarint32(in);
        byte type = in.readByte();
        int requestId = readRawVarint32(in);
        byte commandKey = in.readByte();
        int length = readRawVarint32(in);
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

    private static int readRawVarint32(ByteBuf buffer) {
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
}
