package com.lzh.game.framework.rookie;

import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * @author zehong.l
 * @since 2024-11-12 11:23
 **/
@Slf4j
public class DataTypeTest {

    @Test
    public void intType() {
        final var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);

        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
            ByteBufUtils.writeInt32(buf, i);
            int read = ByteBufUtils.readInt32(buf);
            if (i != read) {
                log.error("error: {} -> {}", i, read);
            }
            buf.clear();
        }
    }

    @Test
    public void longType() {
        var random = new Random();
        final var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            long num = random.nextLong(Long.MIN_VALUE, Long.MAX_VALUE);
            ByteBufUtils.writeInt64(buf, num);
            long read = ByteBufUtils.readInt64(buf);
            if (num != read) {
                log.error("error: {} -> {}", num, read);
            }
            buf.clear();
        }
    }

    @Test
    public void doubleType() {
        var random = new Random();
        final var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
        for (double i = 0; i < Double.MAX_VALUE; i++) {
            double num = random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
            ByteBufUtils.writeFloat64(buf, num);
            double read = ByteBufUtils.readFloat64(buf);
            if (num != read) {
                log.error("error: {} -> {}", num, read);
            }
            buf.clear();
        }
    }

    @Test
    public void floatType() {
        var random = new Random();
        final var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
        for (float i = 0; i < Float.MAX_VALUE; i++) {
            float num = random.nextFloat(Float.MIN_VALUE, Float.MAX_VALUE);
            ByteBufUtils.writeFloat32(buf, num);
            double read = ByteBufUtils.readFloat32(buf);
            if (num != read) {
                log.error("error: {} -> {}", num, read);
            }
            buf.clear();
        }
    }
}
