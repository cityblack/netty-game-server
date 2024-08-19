package com.lzh.game.framework.resource;

import com.lzh.game.framework.resource.data.ResourceManager;
import com.lzh.game.framework.resource.resource.ConfigValueResource;
import com.lzh.game.framework.resource.resource.TestItemResource;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {StorageTest.class})
@SpringBootApplication
@Slf4j
public class StorageTest {

    @Static
    private Storage<Integer, TestItemResource> itemResourceStorage;

    @Autowired
    private ResourceManager manageHandler;

    @Static("DICE_RANGE")
    private ConfigValueResource<Integer[]> dice;

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
        List<TestItemResource> list = itemResourceStorage.getIndex(TestItemResource.INDEX, "新产品1");
        log.info("{}", list);
    }

    @Test
    public void get() {
        log.info("dice:{}", JsonUtils.toJson(dice.getValue()));
    }
}
