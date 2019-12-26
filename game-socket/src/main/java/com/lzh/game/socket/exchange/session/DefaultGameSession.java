package com.lzh.game.socket.exchange.session;

import io.netty.channel.Channel;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultGameSession implements GameSession, Serializable {

    private static final long serialVersionUID = 8359226998972303381L;
    private transient Channel channel;
    private String id;
    private String remoteAddress;
    private Map<String, Object> attribute = new ConcurrentHashMap<>();
    private transient AtomicBoolean opened = new AtomicBoolean(true);
    private transient Instant lastAccessTime;
    private transient Instant creationTime;
    private Integer port;

    public DefaultGameSession(Channel channel) {
        this.channel = channel;
        this.init(channel);
    }

    /**
     * 可能在序列化的时候使用
     */
    public DefaultGameSession() {
    }

    private void init(Channel channel) {
        String[] split = channel.remoteAddress().toString().split(":");
        if (split.length > 1) {
            this.remoteAddress = split[0];
            this.port = Integer.valueOf(split[1]);
        }

        this.id = channel.id().asLongText();
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Channel getChannel() {

        return this.channel;
    }

    @Override
    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public Instant getLastAccessTime() {
        return lastAccessTime;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attribute;
    }

    @Override
    public void setAttribute(String attributeKey, Object attributeValue) {
        this.attribute.put(attributeKey,attributeValue);
    }

    @Override
    public boolean opened() {
        return opened.get();
    }

    @Override
    public void close() {
        close(null);
    }

    @Override
    public void close(SessionCloseListener<Session> listener) {
        if (Objects.nonNull(listener)) {
            getChannel().close().addListener(data -> {
                if (data.isSuccess()) {
                    listener.onSuccess(this);
                } else {
                    listener.onFail(this, data.cause());
                }
                listener.onComplete(this);
            });
        }
        this.attribute.clear();
        this.opened.set(false);
    }

    @Override
    public void write(Object data) {
        channel.writeAndFlush(data);
    }

    @Override
    public Integer getPort() {
        return port;
    }

    public void setCreationTime() {
        this.creationTime = Instant.now();
    }

    public void setLastAccessTime() {
        this.lastAccessTime = Instant.now();
    }


}
