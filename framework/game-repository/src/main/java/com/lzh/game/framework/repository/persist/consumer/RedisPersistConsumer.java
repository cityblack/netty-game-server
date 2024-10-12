//package com.lzh.game.framework.repository.db.persist;
//
//import com.lzh.game.framework.repository.db.Element;
//import com.lzh.game.framework.repository.db.repository.PersistRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.connection.MessageListener;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
///**
// * Redis queue listen
// */
//@Slf4j
//public class RedisPersistConsumer extends DefaultPersistConsumer implements MessageListener, PersistConsumer {
//
//    private RedisSerializer<?> valueSerialize;
//
//    private RedisSerializer<?> stringSerialize;
//
//    public RedisPersistConsumer(RedisSerializer<?> valueSerialize, PersistRepository persistRepository) {
//        super(persistRepository);
//        this.valueSerialize = valueSerialize;
//        stringSerialize = new StringRedisSerializer();
//    }
//
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//
//        if (log.isDebugEnabled()) {
//            log.debug("Get persist message {}", message);
//        }
//        Element element = parseMessage(message);
//        onConsumer(element);
//    }
//
//    private Element parseMessage(Message message) {
//        return JsonUtils.toObj(new String(message.getBody()), Element.class);//(Element) valueSerialize.deserialize(message.getBody());
//    }
//
//}
