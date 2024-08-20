package com.lzh.game.framework.resource;

import com.lzh.game.framework.resource.resource.ConfigValueResource;
import com.lzh.game.framework.resource.resource.ItemResource;
import com.lzh.game.framework.resource.storage.IntKeyStorage;
import com.lzh.game.framework.resource.storage.Storage;
import com.lzh.game.framework.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {StorageTest.class})
@SpringBootApplication
@Slf4j
public class StorageTest {

    @Static
    private IntKeyStorage<ItemResource> itemResourceStorage;

    @Static
    private Storage<Integer, ItemResource> integerItemResourceStorage;

    @Static("DICE_RANGE")
    private ConfigValueResource<Integer[]> dice;

    @Test
    public void getAll() {
        log.info("{}", itemResourceStorage.getAll());
    }

    @Test
    public void getUnique() {
        log.info("{}", itemResourceStorage.get(10001));
        log.info("{}", itemResourceStorage.get(51));
        log.info("{}", integerItemResourceStorage.get(51));
    }

    @Test
    public void getIndex() {
        List<ItemResource> list = itemResourceStorage.getIndex(ItemResource.INDEX, "新产品1");
        log.info("{}", list);
    }

    @Test
    public void get() {
        log.info("dice:{}", JsonUtils.toJson(dice.getValue()));
    }
}
