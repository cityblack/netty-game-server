package com.lzh.netty.socket;

import com.lzh.netty.start.App;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class AppTest {
    @Autowired
    private ReactiveRedisTemplate<String,User> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public void contain() {
        System.out.println("tes");
        /*User u2 = redisTemplate.opsForValue().get("lzh").block();
        System.out.println(u.equals(u2));
        stringRedisTemplate.opsForValue().set("lzh","123");
        System.out.println(stringRedisTemplate.opsForValue().get("lzh"));*/
    }

    @Data
    public class User implements Serializable {
        private static final long serialVersionUID = 151235887726740867L;
        private String id;
        private String name;
    }
}
