package com.lzh.game.socket.core.session;

import io.netty.channel.Channel;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSession implements Session {

    private transient Channel channel;
    private String id;
    private String remoteAddress;
    private Map<String, Object> attribute = new ConcurrentHashMap<>();
    private transient AtomicBoolean opened = new AtomicBoolean(true);
    private transient Instant lastAccessTime;
    private transient Instant creationTime;
    private Integer port;

    protected AbstractSession(Channel channel) {
        this.channel = channel;
        this.init(channel);
    }

    protected void init(Channel channel) {
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
    public void updateLastAccessTime() {
        this.lastAccessTime = Instant.now();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attribute;
    }

    @Override
    public void setAttribute(String attributeKey, Object attributeValue) {
        this.attribute.put(attributeKey, attributeValue);
    }

    @Override
    public boolean opened() {
        return opened.get();
    }

    @Override
    public void close() {
        this.opened.set(false);
        this.channel.close();
        this.attribute.clear();
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
}
