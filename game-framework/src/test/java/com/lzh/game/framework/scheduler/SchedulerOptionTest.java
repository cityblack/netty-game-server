package com.lzh.game.framework.scheduler;

import com.lzh.game.framework.App;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})
@Slf4j
public class SchedulerOptionTest {

    @Autowired
    private SchedulerOption schedulerOption;

    @Test
    public void schedule() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        schedulerOption.schedule(task -> {
            log.info("do this");
            ((CountDownLatch)task.getJobDetail().getJobDataMap().get("down")).countDown();
        }, SchedulerParam.of("??").addParam("down", latch), 10000);
        latch.await();
    }

    @Test
    public void scheduleAtFixedRate() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        schedulerOption.scheduleAtFixedRate(task -> log.info("?xx? hhhh"), SchedulerParam.of("x"), 1000, 3000);
        latch.await();
    }

    @Test
    public void scheduleWithCron() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        schedulerOption.scheduleWithCron(task -> log.info("?xx? hhhh"), SchedulerParam.of("x"), "0/5 * * * * ?");
        latch.await();
    }
}
