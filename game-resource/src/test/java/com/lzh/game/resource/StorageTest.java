package com.lzh.game.resource;

import com.lzh.game.resource.data.ResourceManageHandler;
import com.lzh.game.resource.resource.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@SpringBootTest(classes = {com.lzh.game.resource.App.class})
@Slf4j
public class StorageTest {

    @Static
    private Storage<Integer, TestItemResource> itemResourceStorage;

    @Static
    private Storage<Integer, TestLockResource> lockResourceStorage;

    @Static
    private Storage<Integer, TestSecondLockResource> secondLockResourceStorage;

    @Static
    private Storage<Integer, TestThirdLockResource> thirdLockResourceStorage;
    @Static
    private Storage<Integer, TestFourLockResource> fourLockResourceStorage;
    @Static
    private Storage<Integer, TestFiveLockResource> fiveLockResourceStorage;
    @Static
    private Storage<Integer, TestSixLockResource> sixLockResourceStorage;
    @Static
    private Storage<Integer, TestSevenLockResource> sevenLockResourceStorage;
    @Autowired
    private ResourceManageHandler manageHandler;

    @Test
    public void getAll() {
        log.info("{}", itemResourceStorage.getAll());
    }

    @Test
    public void getUnique() {
        log.info("{}", itemResourceStorage.get(10001));
    }

    @Test
    public void getIndex() {
//        addResource();
        List<TestItemResource> list = itemResourceStorage.getIndex(TestItemResource.INDEX, "新产品1");
        log.info("{}", list);
    }

    @Test
    public void get() {
    }

    @Autowired
    private MongoTemplate template;

    @Test
    public void addResource() {
        TestItemResource resource = new TestItemResource();
        resource.setKey(10001);
        resource.setName("新产品");
        resource.setPile(true);
        resource.setType(1);
        TestItemResource resource2 = new TestItemResource();
        resource2.setKey(10002);
        resource2.setName("测试");
        resource2.setPile(true);
        resource2.setType(2);
        template.save(resource);
        template.save(resource2);
    }

    @Test
    public void addCollectItem() {
        IntStream.range(0, 3000).mapToObj(e -> {
            TestItemResource resource = new TestItemResource();
            resource.setKey(e);
            resource.setName("新产品");
            resource.setPile(true);
            resource.setType(1);
            return resource;
        }).forEach(template::save);
        IntStream.range(0, 3000).mapToObj(e -> {
            TestLockResource resource = new TestLockResource();
            resource.setId(e);
            resource.setLock(true);
            resource.setName("资源2");
            return resource;
        }).forEach(template::save);
        IntStream.range(0, 3000).forEach(e -> {
            TestSecondLockResource resource = new TestSecondLockResource();
            resource.setId(e);
            resource.setName("资源2");
            resource.setOrder(2);
            template.save(resource);

            TestThirdLockResource thirdLockResource = new TestThirdLockResource();
            thirdLockResource.setId(e);
            thirdLockResource.setName("资源3");
            thirdLockResource.setOrder(3);
            template.save(thirdLockResource);

            TestFourLockResource fourLockResource = new TestFourLockResource();
            fourLockResource.setId(e);
            fourLockResource.setName("资源4");
            fourLockResource.setOrder(4);
            template.save(fourLockResource);

            TestFiveLockResource fiveLockResource = new TestFiveLockResource();
            fiveLockResource.setId(e);
            fiveLockResource.setName("资源5");
            fiveLockResource.setOrder(5);
            template.save(fiveLockResource);

            TestSixLockResource sixLockResource = new TestSixLockResource();
            sixLockResource.setId(e);
            sixLockResource.setName("资源6");
            sixLockResource.setOrder(6);
            template.save(sixLockResource);

            TestSevenLockResource sevenLockResource = new TestSevenLockResource();
            sevenLockResource.setId(e);
            sevenLockResource.setName("资源7");
            sevenLockResource.setOrder(7);
            template.save(sevenLockResource);
        });
    }

    @Test
    public void lockReadTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(8);
        ExecutorService service = Executors.newFixedThreadPool(11);
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(itemResourceStorage::get);
            System.out.println("special " + String.valueOf(System.currentTimeMillis() - time));
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(itemResourceStorage::get);
            System.out.println("special " + String.valueOf(System.currentTimeMillis() - time));
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(itemResourceStorage::get);
            System.out.println("special " + String.valueOf(System.currentTimeMillis() - time));
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(lockResourceStorage::get);
            System.out.println(System.currentTimeMillis() - time);
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(secondLockResourceStorage::get);
            System.out.println(System.currentTimeMillis() - time);
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(thirdLockResourceStorage::get);
            System.out.println(System.currentTimeMillis() - time);
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(fourLockResourceStorage::get);
            System.out.println(System.currentTimeMillis() - time);
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(fiveLockResourceStorage::get);
            System.out.println(System.currentTimeMillis() - time);
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(sixLockResourceStorage::get);
            System.out.println(System.currentTimeMillis() - time);

        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(sevenLockResourceStorage::get);
            System.out.println(System.currentTimeMillis() - time);
        });

        latch.await();
    }

    @Test
    public void readWrite() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService service = Executors.newFixedThreadPool(3);
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(itemResourceStorage::get);
            System.out.println("read:" + String.valueOf(System.currentTimeMillis() - time));
            latch.countDown();
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 10000).forEach(itemResourceStorage::get);
            System.out.println("read:" + String.valueOf(System.currentTimeMillis() - time));
            latch.countDown();
        });
        service.submit(() -> {
            long time = System.currentTimeMillis();
            IntStream.range(0, 100).forEach(e -> manageHandler.reload(new Class[]{TestItemResource.class}));
            System.out.println("reload: " + String.valueOf(System.currentTimeMillis() - time));
            latch.countDown();
        });
        latch.await();
    }
}
