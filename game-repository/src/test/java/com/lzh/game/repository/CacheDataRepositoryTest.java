package com.lzh.game.repository;

import com.lzh.game.repository.entity.Common;
import com.lzh.game.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = {com.lzh.game.repository.App.class})
@Slf4j
public class CacheDataRepositoryTest {

    private static final String USER_ID = "netty.game.user";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Repository
    private DataRepository<String, User> dataRepository;

    @Repository
    private DataRepository<String, Common> commonDataRepository;

    @Test
    public void get() {
        User user = dataRepository.get(USER_ID);
        Assert.notNull(user, "can't find user");
    }

    @Test
    public void load() {
        User user = dataRepository.load(USER_ID);
        Assert.notNull(user, "can't find user");
    }

    @Test
    public void loadOrCreate() {
        User user = getUser(USER_ID);
        Assert.notNull(user, "can't find user");
    }

    @Test
    public void update() {
        User user = getUser(USER_ID);
        user.setAge(22);
        dataRepository.update(user);
    }

    private User getUser(String key) {
        return dataRepository.loadOrCreate(key, User::createUser);
    }

    @Test
    public void clear() {
    }

    @Test
    public void del() {
        final String keyValue = "1008611";
        commonDataRepository.deleter(keyValue);
    }

    @Test
    public void add() {
        final String keyValue = "1008611";
        Common common = new Common();
        common.setId(keyValue);
        commonDataRepository.save(common);
    }
}
