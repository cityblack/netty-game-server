package com.lzh.game.framework.socket.core.protocol.message;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zehong.l
 * @since 2024-04-07 14:20
 **/
public class DefaultMessageManager implements MessageManager {

    private Map<Integer, MessageDefine> msg;

    public DefaultMessageManager(Map<Integer, MessageDefine> msg) {
        this.msg = msg;
    }

    public MessageDefine findDefine(int msgId) {
        return this.msg.get(msgId);
    }

    public int getSerializeType(int msgId) {
        MessageDefine define = msg.get(msgId);
        return Objects.isNull(define) ? 0 : define.getSerializeType();
    }

    public boolean hasMessage(int msgId) {
        return this.msg.containsKey(msgId);
    }

    public void registerMessage(MessageDefine define) {
        if (this.msg.containsKey(define.getMsgId())) {
            throw new RuntimeException(define.getMsgId() + " msg id already existed. current class: " + define.getMsgClass().getName());
        }
        this.msg.put(define.getMsgId(), define);
    }

    public void addMessage(Class<?> message) {
        registerMessage(classToDefine(message));
    }

    public static MessageDefine classToDefine(Class<?> msg) {
        Protocol protocol = msg.getAnnotation(Protocol.class);
        if (Objects.isNull(protocol)) {
            throw new IllegalArgumentException("register message " + msg.getSimpleName() + "@Protocol is null");
        }
        return new MessageDefine()
                .setMsgId(protocol.value())
                .setMsgClass(msg)
                .setSerializeType(protocol.serializeType())
                .setProtocolType(protocol.protocolType());
    }
}
