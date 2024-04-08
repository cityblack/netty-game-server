package com.lzh.game.socket.core.message;

import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;
import java.util.Objects;

/**
 * @author zehong.l
 * @date 2024-04-07 14:20
 **/
public class MessageManager {

    private Map<Integer, MessageDefine> msg;

    private MessageManager(Map<Integer, MessageDefine> msg) {
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

    }

    public static MessageDefine classToDefine(Class<?> msg) {
        Protocol protocol = AnnotationUtils.getAnnotation(msg, Protocol.class);
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
