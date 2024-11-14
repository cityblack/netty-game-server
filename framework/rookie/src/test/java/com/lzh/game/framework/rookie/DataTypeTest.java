package com.lzh.game.framework.rookie;

import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zehong.l
 * @since 2024-11-12 11:23
 **/
@Slf4j
public class DataTypeTest {

    @Test
    public void intType() throws InterruptedException {
        numberTest(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    @Test
    public void longType() throws InterruptedException {
        numberTest(Long.MIN_VALUE, Long.MAX_VALUE, false);
    }

    public void numberTest(long min, long max, boolean int32) throws InterruptedException {
        var tasks = computeNumberTask(min, max, int32);
        CountDownLatch latch = new CountDownLatch(tasks.size());
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (long[] task : tasks) {
            service.submit(() -> {
                try {
                    final var buf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(10);
                    long start = task[0], end = task[1];
                    for (long c = start; c < end; c++) {
                        if (int32) {
                            ByteBufUtils.writeInt32(buf, (int) c);
                        } else {
                            ByteBufUtils.writeInt64(buf, c);
                        }
                        long read = int32 ? ByteBufUtils.readInt32(buf) : ByteBufUtils.readInt64(buf);
                        buf.clear();
                        if (c != read) {
                            log.error("error: {} -> {}", c, read);
                        }
                    }
                    log.info("star: {} end: {}", start, end);
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    private List<long[]> computeNumberTask(long min, long max, boolean int32) {
        long num = int32 ? 2 << 25 : (long) 2 << 58;
        List<long[]> tasks = new LinkedList<>();

        for (long i = min; ; i += num) {
            long end;
            if (i > 0 && (i + num < 0)) {
                end = max;
            } else {
                end = i + num - 1;
            }
            long[] numbers = new long[2];
            numbers[0] = i;
            numbers[1] = end;
            tasks.add(numbers);
            if (i > 0 && (i + num < 0 || i + num > max)) {
                break;
            }
        }
        return tasks;
    }
}
