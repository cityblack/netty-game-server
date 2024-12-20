package com.lzh.game.framework.repository.persist.queue;

//@Slf4j
//public class RedisPersistQueue implements Persist {
//
//    private StringRedisTemplate redisTemplate;
//
//    private String top;
//
//    public RedisPersistQueue(StringRedisTemplate redisTemplate, String top) {
//        this.redisTemplate = redisTemplate;
//        this.top = top;
//    }
//
//    @Override
//    public void put(Element element) {
//
//        if (log.isDebugEnabled()) {
//            log.debug("Persist put data:{}", element);
//        }
//        redisTemplate.convertAndSend(top, element);
//    }
//
//    @Override
//    public void shutDown() {
//        this.redisTemplate = null;
//        this.top = null;
//    }
//
//}
