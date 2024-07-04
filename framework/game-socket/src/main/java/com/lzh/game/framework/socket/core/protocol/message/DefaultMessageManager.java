package com.lzh.game.framework.socket.core.protocol.message;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author zehong.l
 * @since 2024-04-07 14:20
 **/
public class DefaultMessageManager implements MessageManager {

    private Map<Short, MessageDefine> msg;

    private final List<Consumer<MessageDefine>> listen = new CopyOnWriteArrayList<>();

    public DefaultMessageManager() {
        this(new ConcurrentHashMap<>());
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
