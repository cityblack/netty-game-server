//package com.lzh.game.framework.repository.cache.redis;
//
//import com.lzh.game.framework.utils.ProtostuffUtils;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.data.redis.serializer.SerializationException;
//
//public class ProtoBufRedisSerializer<T> implements RedisSerializer<T> {
//
//    private Class<T> type;
//
//    public ProtoBufRedisSerializer(Class<T> type) {
//        this.type = type;
//    }
//
//    @Override
//    public byte[] serialize(T t) throws SerializationException {
//        if (t == null) {
//            return new byte[0];
//        } else {
//            try {
//                return ProtostuffUtils.serialize(t);
//            } catch (Exception var3) {
//                throw new SerializationException("Could not serialize: " + var3.getMessage(), var3);
//            }
//        }
//    }
//
//    @Override
//    public T deserialize(byte[] bytes) throws SerializationException {
//        if (bytes != null && bytes.length != 0) {
//            try {
//                return ProtostuffUtils.deSerialize(bytes, type);
//            } catch (Exception var3) {
//                throw new SerializationException("Could not deserialize: " + var3.getMessage(), var3);
//            }
//        } else {
//            return null;
//        }
//    }
//}
