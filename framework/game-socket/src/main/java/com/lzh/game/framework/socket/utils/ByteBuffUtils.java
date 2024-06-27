package com.lzh.game.framework.socket.utils;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;

/**
 * use protocol buff 3
 * @author zehong.l
 * @since 2024-04-11 14:52
 **/
public class ByteBuffUtils {

    public static int readInt(ByteBuf byteBuf) {
        return byteBuf.readInt();
    }

//    public static long readLong(ByteBuf byteBuf) {
//
//    }
//
//    public static double readDouble(ByteBuf byteBuf) {
//
//    }

    public static void writeRawVarint32(ByteBuf out, int value) {
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

    public static int readRawVarint32(ByteBuf buffer) {
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
