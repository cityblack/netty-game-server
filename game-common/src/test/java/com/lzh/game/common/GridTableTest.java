package com.lzh.game.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class GridTableTest {

    @Test
    public void getItem() {
    }

    @Test
    public void removeItem() {

    }

    @Test
    public void addItem() {
        int value = 1008611;

        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer();
        writeRawVarint32(byteBuf, value);
//        System.out.println(readRawVarint32(byteBuf));

        int len = byteBuf.readableBytes();
        byte[] bs = new byte[len];
        byteBuf.readBytes(bs);
        for (byte b : bs) {
            System.out.println(b);
        }
//        System.out.println((byte)198);
//        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
//        byteBuf.writeBytes(new byte[]{(byte) 198, (byte) 143, 123, 0});
//        System.out.println(readRawVarint32(byteBuf));
    }

    private static int readRawVarint32(ByteBuf buffer) {
        if (!buffer.isReadable()) {
            return 0;
        }
        buffer.markReaderIndex();
        byte tmp = buffer.readByte();
        if (tmp >= 0) {
            return tmp;
        } else {
            int result = tmp & 127;
            if (!buffer.isReadable()) {
                buffer.resetReaderIndex();
                return 0;
            }
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
