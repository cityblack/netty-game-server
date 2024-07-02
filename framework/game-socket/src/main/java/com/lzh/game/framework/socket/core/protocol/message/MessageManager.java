package com.lzh.game.framework.socket.core.protocol.message;

import java.util.function.Consumer;

/**
 * @author zehong.l
 * @since 2024-06-27 18:42
 **/
public interface MessageManager {

    MessageDefine findDefine(short msgId);

    int getSerializeType(short msgId);

    boolean hasMessage(short msgId);

    void registerMessage(MessageDefine define);

    void addMessage(Class<?> message);

    void addRegisterListen(Consumer<MessageDefine> consumer);
}
