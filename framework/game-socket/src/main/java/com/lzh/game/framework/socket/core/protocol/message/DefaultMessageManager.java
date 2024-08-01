package com.lzh.game.framework.socket.core.protocol.message;

import com.lzh.game.framework.socket.utils.Constant;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * @author zehong.l
 * @since 2024-04-07 14:20
 **/
public class DefaultMessageManager implements MessageManager {

    private Map<Short, MessageDefine> msg;

    private final List<Consumer<MessageDefine>> listen = new CopyOnWriteArrayList<>();

    private Map<Class<?>, MessageDefine> defaultDefined;

    public DefaultMessageManager() {
        this(new ConcurrentHashMap<>());
        initRegister();
    }

    private void initRegister() {
        var types = new Class<?>[]{
                void.class, boolean.class, byte.class, char.class, short.class, int.class,
                float.class, long.class, double.class, Void.class, Boolean.class, Byte.class,
                Character.class, Short.class, Integer.class, Float.class, Long.class, Double.class,
                String.class, boolean[].class, byte[].class, char[].class, short[].class, int[].class,
                float[].class, long[].class, double[].class, String[].class, Object[].class, ArrayList.class,
                HashMap.class, HashSet.class, Class.class, Object.class, LinkedList.class, TreeSet.class,
                LinkedHashMap.class, TreeMap.class, Date.class, Timestamp.class, LocalDateTime.class, Instant.class,
                BigInteger.class, BigDecimal.class, Optional.class, OptionalInt.class,
                Boolean[].class, Byte[].class, Short[].class, Character[].class,
                Integer[].class, Float[].class, Long[].class, Double[].class, ConcurrentHashMap.class,
                ArrayBlockingQueue.class, LinkedBlockingQueue.class, AtomicBoolean.class, AtomicInteger.class,
                AtomicLong.class, AtomicReference.class, Throwable.class, StackTraceElement.class, Exception.class, RuntimeException.class,
                NullPointerException.class, IOException.class, IllegalArgumentException.class, IllegalStateException.class,
                IndexOutOfBoundsException.class, ArrayIndexOutOfBoundsException.class
        };
        Map<Class<?>, MessageDefine> defaultDefined = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            var defined = new MessageDefine()
                    .setMsgId((short) i)
                    .setMsgClass(types[i]);
            registerMessage(defined);
            defaultDefined.put(defined.getMsgClass(), defined);
        }
        this.defaultDefined = defaultDefined;
    }

    public DefaultMessageManager(Map<Short, MessageDefine> msg) {
        this.msg = msg;
    }

    public MessageDefine findDefine(short msgId) {
        return this.msg.get(msgId);
    }

    public int getSerializeType(short msgId) {
        MessageDefine define = msg.get(msgId);
        return Objects.isNull(define) ? 0 : define.getSerializeType();
    }

    public boolean hasMessage(short msgId) {
        return this.msg.containsKey(msgId);
    }

    public void registerMessage(MessageDefine define) {
        if (Objects.isNull(define)) {
            throw new IllegalArgumentException("Register define is null!");
        }
        var old = this.msg.get(define.getMsgId());
        if (Objects.nonNull(old) && old.getMsgClass() != define.getMsgClass()) {
            throw new RuntimeException(define.getMsgId() + " msg id already existed. current class: " + define.getMsgClass().getName());
        }
        this.msg.put(define.getMsgId(), define);
        this.listen.forEach(e -> e.accept(define));
    }

    public void addMessage(Class<?> message) {
        registerMessage(classToDefine(message));
    }

    @Override
    public void addRegisterListen(Consumer<MessageDefine> consumer) {
        listen.add(consumer);
    }

    @Override
    public int count() {
        return msg.size();
    }

    @Override
    public MessageDefine findDefaultDefined(Class<?> type) {
        return defaultDefined.get(type);
    }

    public static MessageDefine classToDefine(Class<?> msg) {
        Protocol protocol = msg.getAnnotation(Protocol.class);
        if (Objects.isNull(protocol)) {
            throw new IllegalArgumentException("register message " + msg.getSimpleName() + "@Protocol is null");
        }
        return new MessageDefine()
                .setMsgId(protocol.value())
                .setMsgClass(msg)
                .setSerializeType(protocol.serializeType());
//                .setProtocolType(protocol.protocolType());
    }
}
