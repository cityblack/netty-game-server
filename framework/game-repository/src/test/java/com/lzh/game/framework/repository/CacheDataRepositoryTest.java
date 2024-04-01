package com.lzh.game.framework.repository;

import com.lzh.game.framework.repository.entity.Common;
import com.lzh.game.framework.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.Assert;

@SpringBootTest(classes = {App.class})
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
