package com.lzh.game.framework.socket.core.protocol.serial;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zehong.l
 * @since 2024-04-07 14:49
 **/
public class MessageSerializeManager {

    private final Map<Integer, MessageSerialize> handlers = new ConcurrentHashMap<>();

    public void registerSerialize(int type, MessageSerialize serialize) {
        handlers.put(type, serialize);
    }

    public MessageSerialize getProtocolSerialize(int type) {
        return handlers.get(type);
    }

    public static MessageSerializeManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {

        private static final MessageSerializeManager INSTANCE = new MessageSerializeManager();
    }

    private MessageSerializeManager() {
    }
}
