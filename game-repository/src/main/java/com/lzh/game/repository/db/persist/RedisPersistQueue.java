package com.lzh.game.repository.db.persist;

import com.lzh.game.repository.db.Element;
import com.lzh.game.repository.db.Persist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
public class RedisPersistQueue implements Persist {

    private StringRedisTemplate redisTemplate;

    private String top;

    public RedisPersistQueue(StringRedisTemplate redisTemplate, String top) {
        this.redisTemplate = redisTemplate;
        this.top = top;
    }

    @Override
    public void put(Element element) {

        if (log.isDebugEnabled()) {
            log.debug("Persist put data:{}", element);
        }
        redisTemplate.convertAndSend(top, element);
    }

    @Override
    public void shutDown() {
        this.redisTemplate = null;
        this.top = null;
    }

}
