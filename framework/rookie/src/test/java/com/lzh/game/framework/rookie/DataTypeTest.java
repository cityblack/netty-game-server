package com.lzh.game.framework.rookie;

import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zehong.l
 * @since 2024-11-12 11:23
 **/
public class DataTypeTest {

    @Test
    public void intType() throws InterruptedException {

        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int num = 2 << 25;
        for (int i = Integer.MIN_VALUE; ; i += num) {
            final var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
            int end;
            if (i > 0 && (i + num < 0)) {
                end = Integer.MAX_VALUE;
            } else {
                end = i + num - 1;
            }
            final int start = i;
            service.submit(() -> {
                for (int c = start; c < end; c++) {
                    ByteBufUtils.writeInt32(buf, c);
                    int read = ByteBufUtils.readInt32(buf);
                    buf.clear();
                    if (c != read) {
                        System.err.println("error: " + c + " -> " + read);
                    }
                }
                System.out.println("star: " + start + " end:" + end);
            });
            if (i > 0 && i + num < 0) {
                break;
            }
        }
        while (service.awaitTermination(20, TimeUnit.SECONDS)) {

        }
    }
}
