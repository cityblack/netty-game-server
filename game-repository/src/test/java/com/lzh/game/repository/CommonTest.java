package com.lzh.game.repository;

import com.lzh.game.common.bean.BeanUtil;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class CommonTest {

    @Test
    public void lock() throws IOException {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:16379")

                .setPassword("as123456");

//        config.useClusterServers()
//                // use "rediss://" for SSL connection
//                //redis://localhost:16379
//                .addNodeAddress("redis://localhost:6379");
//                //.setPassword("as123456");
        RedissonClient client = Redisson.create(config);

        /*List<Integer> list = client.getList("lzh", new JsonJacksonCodec());
        System.out.println(list);
        list.add(1);
        list.add(2);
        list.add(3);*/
        /*User newUser = User.createUser();
        User a = (User) client.getBucket("lx", new JsonJacksonCodec()).getAndSet(newUser);
        System.out.println(a);*/
        /*Map<String, Object> map = transfer(newUser);
        System.out.println(map);
        Map<String, Object> objectMap = client.getMap("xx", new JsonJacksonCodec());
//        objectMap.forEach((k,v) -> System.out.println(k + " " + v));
       // objectMap.putAll(map);
        User user = BeanUtil.mapToBean(objectMap, User.class);
        user.getItems().add(new User.Item());
        System.out.println(user);*/
        Map<String, Object> objectMap = client.getMap("xx", new JsonJacksonCodec());
        objectMap.clear();
    }

    private Map<String, Object> transfer(Object o) {
        return BeanUtil.beanToMap(o);
    }

    @Test
    public void s() {
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
