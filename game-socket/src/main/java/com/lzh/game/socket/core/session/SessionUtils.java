package com.lzh.game.socket.core.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Objects;

public class SessionUtils {

    private static final AttributeKey<Session> CHANNEL_SESSION_KEY = AttributeKey.valueOf("channel-session");

    public static Session channelGetSession(Channel channel) {
        if (Objects.isNull(channel)) {
            return null;
        }
        return channel.attr(CHANNEL_SESSION_KEY).get();
    }

    public static void channelBindSession(Channel channel, Session session) {
        if (Objects.isNull(channel) || Objects.isNull(session)) {
            throw new RuntimeException("channel or session is null.");
        }
        channel.attr(CHANNEL_SESSION_KEY).compareAndSet(null, session);
    }

    public static void channelUnbindSession(Channel channel) {
        if (Objects.isNull(channel)) {
            return;
        }
        channel.attr(CHANNEL_SESSION_KEY).set(null);
    }
}
