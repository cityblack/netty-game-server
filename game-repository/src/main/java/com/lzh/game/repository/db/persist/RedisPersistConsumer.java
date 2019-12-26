package com.lzh.game.repository.db.persist;

import com.alibaba.fastjson.JSON;
import com.lzh.game.repository.db.Element;
import com.lzh.game.repository.db.PersistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

/**
 * Redis queue listen
 */
@Slf4j
public class RedisPersistConsumer extends DefaultPersistConsumer implements MessageListener, PersistConsumer {

    private RedisSerializer<?> valueSerialize;

    private RedisSerializer<?> stringSerialize;

    public RedisPersistConsumer(RedisSerializer<?> valueSerialize, PersistRepository persistRepository) {
        super(persistRepository);
        this.valueSerialize = valueSerialize;
        stringSerialize = new StringRedisSerializer();
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        if (log.isDebugEnabled()) {
            log.debug("Get persist message {}", message);
        }
        Element element = parseMessage(message);
        onConsumer(element);
    }

    private Element parseMessage(Message message) {
        return JSON.parseObject(new String(message.getBody()), Element.class);//(Element) valueSerialize.deserialize(message.getBody());
    }

}
