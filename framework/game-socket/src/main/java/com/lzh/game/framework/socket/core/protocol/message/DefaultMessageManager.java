package com.lzh.game.framework.socket.core.protocol.message;

import com.lzh.game.framework.common.collection.IdentityMap;
import com.lzh.game.framework.socket.core.SocketProperties;
import com.lzh.game.framework.socket.Constant;
import io.netty.util.collection.ShortObjectHashMap;
import io.netty.util.collection.ShortObjectMap;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author zehong.l
 * @since 2024-04-07 14:20
 **/
public class DefaultMessageManager implements MessageManager {

    private final Map<String, Consumer<MessageDefine>> listen = new ConcurrentHashMap<>();

    private final IdentityMap<Class<?>, MessageDefine> classContain;

    private final ShortObjectMap<MessageDefine> idContain;

    public DefaultMessageManager(SocketProperties properties) {
        this.classContain = new IdentityMap<>();
        this.idContain = new ShortObjectHashMap<>();
        initRegister(properties);
    }

    private void initRegister(SocketProperties properties) {
        registerDefault();
        var handler = new MessageLoadHandler();
        var list = handler.load(properties.getProtocolScanner());
        list.forEach(this::registerMessage);
    }

    private void registerDefault() {
        for (Class<?> clz : Constant.INNER_MSG) {
            registerMessage(clz);
        }
    }

    public MessageDefine findDefine(short msgId) {
        return idContain.get(msgId);
    }

    public int getSerializeType(short msgId) {
        MessageDefine define = idContain.get(msgId);
        return Objects.isNull(define) ? 0 : define.getSerializeType();
    }

    public boolean hasDefined(short msgId) {
        return idContain.containsKey(msgId);
    }

    public void registerMessage(MessageDefine define) {
        if (Objects.isNull(define)) {
            throw new IllegalArgumentException("Register define is null!");
        }
        var old = idContain.get(define.getMsgId());
        if (Objects.nonNull(old) && old.getMsgClass() != define.getMsgClass()) {
            throw new RuntimeException(define.getMsgId() + " msg id already existed. current class: " + define.getMsgClass().getName());
        }
        idContain.put(define.getMsgId(), define);
        classContain.put(define.getMsgClass(), define);
        listen.values().forEach(e -> e.accept(define));
    }

    public void registerMessage(Class<?> message) {
        if (!message.isAnnotationPresent(Protocol.class)) {
            throw new IllegalArgumentException(message.getName() + "No have @Protocol.");
        }
        registerMessage(MessageManager.classToDefine(message));
    }

    @Override
    public void addRegisterListen(String name, Consumer<MessageDefine> consumer) {
        listen.put(name, consumer);
        idContain.values().forEach(consumer);
    }

    @Override
    public void removeListen(String name) {
        listen.remove(name);
    }

    @Override
    public int count() {
        return idContain.size();
    }

    @Override
    public MessageDefine findDefine(Class<?> type) {
        return classContain.get(type);
    }

}
