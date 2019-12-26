package com.lzh.game.resource;

import com.lzh.game.resource.resource.TestItemResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.lzh.game.resource.App.class})
@Slf4j
public class StorageTest {

    @Static
    private Storage<Integer, TestItemResource> itemResourceStorage;

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
}