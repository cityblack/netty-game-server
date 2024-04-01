package com.lzh.game.socket.core.session;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class SessionUtils {

    private static final AttributeKey<Session> CHANNEL_SESSION_KEY = AttributeKey.newInstance("channel-session");

    public static final AttributeKey<CompletableFuture<Session>> CHANNEL_FUTURE_KEY = AttributeKey.newInstance("channel-future-session");

    public static CompletableFuture<Session> getBindFuture(Channel channel) {
        if (Objects.isNull(channel)) {
            throw new NullPointerException("Channel is null.");
        }
        channel.attr(CHANNEL_FUTURE_KEY).setIfAbsent(new CompletableFuture<>());
        return channel.attr(CHANNEL_FUTURE_KEY).get();
    }

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
        getBindFuture(channel).complete(session);
    }

    public static void channelUnbindSession(Channel channel) {
        if (Objects.isNull(channel)) {
            return;
        }
        channel.attr(CHANNEL_SESSION_KEY).set(null);
        channel.attr(CHANNEL_FUTURE_KEY).set(null);
    }
}
