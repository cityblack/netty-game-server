package com.lzh.game.framework.socket.core.protocol.message;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author zehong.l
 * @since 2024-06-27 18:42
 **/
public interface MessageManager {

    MessageDefine findDefine(short msgId);

    int getSerializeType(short msgId);

    boolean hasDefined(short msgId);

    void registerMessage(MessageDefine define);

    void registerMessage(Class<?> message);

    void addRegisterListen(String name, Consumer<MessageDefine> consumer);

    void removeListen(String name);

    int count();

    MessageDefine findDefine(Class<?> type);

    static MessageDefine classToDefine(Class<?> msg) {
        Protocol protocol = msg.getAnnotation(Protocol.class);
        if (Objects.isNull(protocol)) {
            throw new IllegalArgumentException("register message " + msg.getSimpleName() + " @Protocol is null");
        }
        return new MessageDefine()
                .setMsgId(protocol.value())
                .setMsgClass(msg)
                .setSerializeType(protocol.serializeType());
//                .setProtocolType(protocol.protocolType());
    }
}
