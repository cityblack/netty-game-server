package com.lzh.game.framework.rookie;

import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.*;
import org.junit.jupiter.api.Test;

/**
 * @author zehong.l
 * @since 2024-09-18 12:07
 **/
public class ByteTest {

    @Test
    public void baseType() {
        var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(20);
        ByteBufUtils.writeBoolean(buf, true);
        ByteBufUtils.writeInt8(buf, (byte) 1);
        ByteBufUtils.writeInt16(buf, (short) 2);
        ByteBufUtils.writeInt32(buf, 3);
        ByteBufUtils.writeInt64(buf, 4L);
        ByteBufUtils.writeChar(buf, 'c');
        ByteBufUtils.writeFloat32(buf, 6.6F);
        ByteBufUtils.writeFloat64(buf, 8.8D);

        System.out.println(buf.writerIndex());
        assert ByteBufUtils.readBoolean(buf);
        assert ByteBufUtils.readInt8(buf) == 1;
        assert ByteBufUtils.readInt16(buf) == 2;
        assert ByteBufUtils.readInt32(buf) == 3;
        assert ByteBufUtils.readInt64(buf) == 4L;
        assert ByteBufUtils.readChar(buf) == 'c';
        assert ByteBufUtils.readFloat32(buf) == 6.6F;
        assert ByteBufUtils.readFloat64(buf) == 8.8D;
    }
}
