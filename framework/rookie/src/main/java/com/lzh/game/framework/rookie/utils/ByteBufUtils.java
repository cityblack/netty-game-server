package com.lzh.game.framework.rookie.utils;

import io.netty.buffer.ByteBuf;

/**
 * Base on netty
 * Java nio is BIG_ENDIAN
 * RawVarInt32/64 LITTLE_ENDIAN
 *
 * @author zehong.l
 * @since 2024-08-30 16:11
 **/
public class ByteBufUtils {

    public static byte readInt8(ByteBuf in) {
        return in.readByte();
    }

    public static void writeInt8(ByteBuf out, byte data) {
        out.writeByte(data);
    }

    public static char readChar(ByteBuf in) {
        return in.readChar();
    }

    public static void writeChar(ByteBuf out, char value) {
        out.writeChar(value);
    }

    public static byte[] readBytes(ByteBuf in, int len) {
        var bytes = new byte[len];
        in.readBytes(bytes);
        return bytes;
    }

    public static void writeBytes(ByteBuf out, byte[] bytes) {
        out.writeBytes(bytes);
    }

    public static boolean readBoolean(ByteBuf in) {
        return in.readBoolean();
    }

    public static void writeBoolean(ByteBuf out, boolean data) {
        out.writeBoolean(data);
    }

    public static short readInt16(ByteBuf in) {
        return in.readShort();
    }

    public static void writeInt16(ByteBuf out, short data) {
        out.writeShort(data);
    }

    public static int readInt32(ByteBuf in) {
        return decodeZigZag32(readRawVarint32(in));
    }

    public static void writeInt32(ByteBuf out, int value) {
        writeRawVarint32(out, encodeZigZag32(value));
    }

    public static long readInt64(ByteBuf in) {
        return decodeZigZag64(readRawVarint64(in));
    }

    public static void writeInt64(ByteBuf out, long value) {
        writeRawVarint64(out, encodeZigZag64(value));
    }

    public static float readFloat32(ByteBuf in) {
        return in.readFloat();
    }

    public static void writeFloat32(ByteBuf out, float data) {
        out.writeFloat(data);
    }

    public static double readFloat64(ByteBuf in) {
        return in.readDouble();
    }

    public static void writeFloat64(ByteBuf out, Double value) {
        out.writeDouble(value);
    }

    public static int encodeZigZag32(int n) {
        return n << 1 ^ n >> 31;
    }

    public static int decodeZigZag32(int n) {
        return n >>> 1 ^ -(n & 1);
    }

    public static long encodeZigZag64(long n) {
        return n << 1 ^ n >> 63;
    }

    public static long decodeZigZag64(long n) {
        return n >>> 1 ^ -(n & 1L);
    }

    public static int readRawVarint32(ByteBuf buffer) {
        fastpath:
        {
            int readerIndex = buffer.readerIndex();
            int x;
            if ((x = buffer.readByte()) >= 0) {
                return x;
            } else if ((x ^= (buffer.readByte() << 7)) < 0) {
                x ^= (~0 << 7);
            } else if ((x ^= (buffer.readByte() << 14)) >= 0) {
                x ^= (~0 << 7) ^ (~0 << 14);
            } else if ((x ^= (buffer.readByte() << 21)) < 0) {
                x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21);
            } else {
                int y = buffer.readByte();
                x ^= y << 28;
                x ^= (~0 << 7) ^ (~0 << 14) ^ (~0 << 21) ^ (~0 << 28);
                if (y < 0
                        && buffer.readByte() < 0
                        && buffer.readByte() < 0
                        && buffer.readByte() < 0
                        && buffer.readByte() < 0
                        && buffer.readByte() < 0) {
                    buffer.readerIndex(readerIndex);
                    break fastpath;
                }
            }
            return x;
        }
        return (int) readRawVarint64SlowPath(buffer);
    }

    public static void writeRawVarint32(ByteBuf out, int value) {
        while ((value & -128) != 0) {
            out.writeByte(value & 127 | 128);
            value >>>= 7;
        }
        out.writeByte(value);
    }

    public static long readRawVarint64(ByteBuf buffer) {
        fastPath:
        {
            int readerIndex = buffer.readerIndex();
            long x;
            int y;
            if ((y = buffer.readByte()) >= 0) {
                return y;
            } else if ((y ^= (buffer.readByte() << 7)) < 0) {
                x = y ^ (~0 << 7);
            } else if ((y ^= (buffer.readByte() << 14)) >= 0) {
                x = y ^ ((~0 << 7) ^ (~0 << 14));
            } else if ((y ^= (buffer.readByte() << 21)) < 0) {
                x = y ^ ((~0 << 7) ^ (~0 << 14) ^ (~0 << 21));
            } else if ((x = y ^ ((long) buffer.readByte() << 28)) >= 0L) {
                x ^= (~0L << 7) ^ (~0L << 14) ^ (~0L << 21) ^ (~0L << 28);
            } else if ((x ^= ((long) buffer.readByte() << 35)) < 0L) {
                x ^= (~0L << 7) ^ (~0L << 14) ^ (~0L << 21) ^ (~0L << 28) ^ (~0L << 35);
            } else if ((x ^= ((long) buffer.readByte() << 42)) >= 0L) {
                x ^= (~0L << 7) ^ (~0L << 14) ^ (~0L << 21) ^ (~0L << 28) ^ (~0L << 35) ^ (~0L << 42);
            } else if ((x ^= ((long) buffer.readByte() << 49)) < 0L) {
                x ^=
                        (~0L << 7)
                                ^ (~0L << 14)
                                ^ (~0L << 21)
                                ^ (~0L << 28)
                                ^ (~0L << 35)
                                ^ (~0L << 42)
                                ^ (~0L << 49);
            } else {
                x ^= ((long) buffer.readByte() << 56);
                x ^=
                        (~0L << 7)
                                ^ (~0L << 14)
                                ^ (~0L << 21)
                                ^ (~0L << 28)
                                ^ (~0L << 35)
                                ^ (~0L << 42)
                                ^ (~0L << 49)
                                ^ (~0L << 56);
                if (x < 0L && buffer.readByte() < 0L) {
                    buffer.readerIndex(readerIndex);
                    break fastPath;
                }
            }
            return x;
        }
        return readRawVarint64SlowPath(buffer);
    }

    public static long readRawVarint64SlowPath(ByteBuf buf) {
        long result = 0;
        for (int shift = 0; shift < 64; shift += 7) {
            final byte b = readInt8(buf);
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
        }
        throw new RuntimeException();
    }

    public static void writeRawVarint64(ByteBuf out, long value) {
        while ((value & -128) != 0) {
            out.writeByte((int) (value & 127 | 128));
            value >>>= 7;
        }
        out.writeByte((byte) value);
    }
}
