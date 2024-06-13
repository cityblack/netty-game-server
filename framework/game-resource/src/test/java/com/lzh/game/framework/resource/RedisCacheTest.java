package com.lzh.game.framework.resource;

import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.framework.resource.data.ResourceModel;
import com.lzh.game.framework.resource.data.cache.RedisResourceCacheFactory;
import com.lzh.game.framework.resource.data.cache.ResourceCache;
import com.lzh.game.framework.resource.resource.TestItemResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {App.class})
@Slf4j
public class RedisCacheTest {

    private RedissonClient client;

    private ResourceCache<Integer, TestItemResource> cache;

    private ResourceModel model;

    {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://120.78.234.195:16379")
                .setPassword("as123456..").setDatabase(0);
        config.setCodec(new JsonJacksonCodec());
        client = Redisson.create(config);
        RedisResourceCacheFactory factory = new RedisResourceCacheFactory((Redisson) client);
        model = ResourceModel.of(TestItemResource.class, TestItemResource.class.getName());
        cache = factory.newCache(TestItemResource.class, model);
    }

    @Test
    public void insert() {

        List<TestItemResource> list = new ArrayList<>();
        TestItemResource resource = new TestItemResource();
        resource.setKey(10001);
        resource.setName("新产品");
        resource.setPile(true);
        resource.setType(1);
        list.add(resource);
        TestItemResource resource2 = new TestItemResource();
        resource2.setKey(10002);
        resource2.setName("测试");
        resource2.setPile(true);
        resource2.setType(2);
        list.add(resource2);
        cache.put(list, model);

        log.info("first item:{}", cache.findById(10001));
        log.info("index item:{}", JsonUtils.toJson(cache.findByIndex(TestItemResource.INDEX, "新产品1")));
    }

    @Test
    public void findAll() {
        log.info("find all =========");
        for (TestItemResource resource : cache.findAll()) {
            log.info("{}", resource.toString());
        }
        log.info("find all end =========");
    }
}
