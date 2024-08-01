package com.lzh.game.framework.socket.core.session.impl;

import com.lzh.game.framework.socket.core.session.Session;
import io.netty.channel.Channel;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSession implements Session {

    private transient Channel channel;
    private String id;
    private String remoteAddress;
    private final Map<String, Object> attribute = new ConcurrentHashMap<>();
    private transient Instant lastAccessTime;
    private transient Instant creationTime;
    private Integer port;

    protected AbstractSession(Channel channel) {
        this(channel, true);
    }

    protected AbstractSession(Channel channel, boolean init) {
        this.channel = channel;
        if (init) {
            this.init(channel);
        }
    }

    protected void init(Channel channel) {
        this.remoteAddress = channel.remoteAddress().toString().replace("/","");
        String[] split = this.remoteAddress.split(":");
        if (split.length > 1) {
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
    public boolean isOpened() {
        return this.channel.isOpen();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive() && isOpened();
    }

    @Override
    public void close() {
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
