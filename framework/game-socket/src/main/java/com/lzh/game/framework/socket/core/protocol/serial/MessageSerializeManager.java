package com.lzh.game.framework.socket.core.protocol.serial;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-04-07 14:49
 **/
public class MessageSerializeManager {

    private final Map<Integer, MessageSerialize> handlers = new HashMap<>();

    public void registerMessage(int type, MessageSerialize handler) {
        handlers.put(type, handler);
    }

    public MessageSerialize getProtocolMessage(int type) {
        return handlers.get(type);
    }

    public static MessageSerializeManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {

        private static MessageSerializeManager INSTANCE = new MessageSerializeManager();
    }

    private MessageSerializeManager() {
    }
}
