package com.lzh.game.framework.socket.core.protocol.message;

/**
 * @author zehong.l
 * @since 2024-06-27 18:42
 **/
public interface MessageManager {

    MessageDefine findDefine(int msgId);

    int getSerializeType(int msgId);

    boolean hasMessage(int msgId);

    void registerMessage(MessageDefine define);

    void addMessage(Class<?> message);
}
