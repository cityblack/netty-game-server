package com.lzh.game.framework.socket.core.session.impl;

import com.lzh.game.framework.socket.core.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.AttributeKey;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractSession implements Session {

    private final transient Channel channel;
    private String id;
    private String remoteAddress;
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
        setCreationTime();
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
    public void setAttribute(String attributeKey, Object attributeValue) {
        this.channel.attr(AttributeKey.valueOf(attributeKey)).set(attributeValue);
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
    }

    @Override
    public ChannelFuture write(Object data) {
        return channel.writeAndFlush(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return (T) this.channel.attr(AttributeKey.valueOf(name)).get();
    }

    @Override
    public boolean hasAttribute(String name) {
        return this.channel.hasAttr(AttributeKey.valueOf(name));
    }

    @Override
    public Integer getPort() {
        return port;
    }

    public void setCreationTime() {
        this.creationTime = Instant.now();
    }

}
