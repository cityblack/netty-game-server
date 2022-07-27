package com.lzh.game.resource;

import com.lzh.game.common.serialization.JsonUtils;
import com.lzh.game.resource.data.ResourceModel;
import com.lzh.game.resource.data.cache.RedisResourceCacheFactory;
import com.lzh.game.resource.data.cache.ResourceCache;
import com.lzh.game.resource.resource.TestItemResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = {com.lzh.game.resource.App.class})
@Slf4j
public class RedisCacheTest {

    private RedissonClient client;

    {
        Config config = new Config();

        config.setCodec(new JsonJacksonCodec());
        client = Redisson.create(config);
    }

    @Test
    public void insert() {

        RedisResourceCacheFactory factory = new RedisResourceCacheFactory((Redisson) client);
        ResourceModel model = ResourceModel.of(TestItemResource.class, TestItemResource.class.getName());
        ResourceCache<Integer, TestItemResource> cache = factory.newCache(TestItemResource.class, model);
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
}
