package com.lzh.game.repository;

import com.alibaba.fastjson.JSON;
import com.lzh.game.common.bean.BeanUtil;
import com.lzh.game.repository.entity.Common;
import com.lzh.game.repository.entity.User;
import com.lzh.game.repository.model.AbstractItem;
import com.lzh.game.repository.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.lzh.game.repository.App.class})
@Slf4j
public class CacheDataRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheDataRepository dataRepository;

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void get() {
        Map<String, Object> map = redissonClient.getMap("xx", new JsonJacksonCodec());
        User user = User.createUser();

        map.putAll(BeanUtil.beanToMap(user));
        System.out.println(map);
    }

    @Test
    public void load() {
        final String uuid = "xxxxxxx";

        User user = dataRepository.loadOrCreate(uuid, User.class, (pk) -> {
            User newUser = new User();
            newUser.setId(pk);
            newUser.setAge(30);
            newUser.setAddress("广东省广州市");
            return newUser;
        });
        user.setTel("110120130140");
        /*PlayerItemBox box = dataRepository.enhanceLoadOrCreate(user.cacheKey(), PlayerItemBox.class
                , itemBoxRepository, e -> {
                    PlayerItemBox box1 = new PlayerItemBox();
                    box1.setPlayer(e);
                    return box1;
                });*/

        AbstractItem item = new AbstractItem();
        item.setName("白色金卡");
        item.setNum(10);
//        box.addItem(item);
        /*box.setUpdateTime(System.currentTimeMillis());
        dataRepository.update(box);*/
        dataRepository.update(user);
    }

    @Test
    public void loadOrCreate() {

        final String keyValue = "102";

        Common value = dataRepository.loadOrCreate(keyValue, Common.class, (key) -> {
            Common common = new Common();
            common.setId(key);
            common.setData(JSON.toJSONString(new User()));
            return common;
        });
        User user = new User();
        user.setAddress("福建宁德");
        String s = JSON.toJSONString(user);
        value.setData(s);

        dataRepository.update(value);
        log.info("{}", value);
    }

    @Test
    public void update() {
        User users = User.createUser();
        String key = "lzh";
        addUser(key, users);
        long time = System.currentTimeMillis();

//        redisTemplate.getConnectionFactory().getConnection()
        IntStream.range(0, 10000).forEach(e -> {
            User user = getUser(key);
            if (user.getItems().size() < 500) {
                user.getItems().add(new User.Item());
            }
            addUser(key, user);
        });
        System.out.println(System.currentTimeMillis() - time);
    }

    private User getUser(String key) {
        //return (User) redisTemplate.opsForValue().get(key);
        //return BeanUtil.mapToBean(redissonClient.getMap(key), User.class);
        return null;
    }

    private void addUser(String key, User user) {
        //redisTemplate.opsForValue().set(key, user);
//        redissonClient.getMap(key, new JsonJacksonCodec()).putAll(BeanUtil.beanToMap(user));
    }

    private Map<String, Object> change(User user) {
        Class<?> c = user.getClass();
        Map<String, Object> change = new HashMap<>(1);
        ReflectionUtils.doWithFields(c, e -> {
            ReflectionUtils.makeAccessible(e);
            change.put(e.getName(), ReflectionUtils.getField(e, user));
        }, e -> Collection.class.isAssignableFrom(e.getType()));
        return change;
    }

    @Test
    public void update1() {
    }

    @Test
    public void clear() {
    }

    @Test
    public void del() {
        final String keyValue = "1008611";
        dataRepository.deleter(keyValue, Common.class);
    }

    @Test
    public void add() {
        final String keyValue = "1008611";
        Common common = new Common();
        common.setId(keyValue);
        dataRepository.addAndSave(keyValue, common);
    }
}