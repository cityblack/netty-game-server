package com.lzh.game.framework.socket.core.protocol.message;

import com.lzh.game.framework.utils.ClassScannerUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zehong.l
 * @since 2024-08-22 10:44
 **/
public class MessageLoadHandler {

    public List<MessageDefine> load(String[] packageNames, int serializeType) {
        var list = new ArrayList<MessageDefine>();
        defaultMessage(list, serializeType);
        loadDefined(list, packageNames);
        return list;
    }

    private void defaultMessage(List<MessageDefine> list, int serializeType) {
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
        for (int i = 0; i < types.length; i++) {
            var defined = new MessageDefine()
                    .setMsgId((short) i)
                    .setMsgClass(types[i])
                    .setSerializeType(serializeType);
            list.add(defined);
        }
    }

    private void loadDefined(List<MessageDefine> list, String[] packageNames) {
        ClassScannerUtils.scanPackage(packageNames, e -> e.isAnnotationPresent(Protocol.class), e -> {
            var defined = MessageManager.classToDefine(e);
            list.add(defined);
        });
    }
}
