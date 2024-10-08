package com.lzh.game.framework.socket.core.session.impl;

import com.lzh.game.framework.socket.core.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureSession implements Session {

    public FutureSession(Future<Session> fact) {
        this.fact = fact;
    }

    private Future<Session> fact;

    @Override
    public String getRemoteAddress() {
        return blockGetSession().getRemoteAddress();
    }

    @Override
    public String getId() {
        return blockGetSession().getId();
    }

    @Override
    public Instant getCreationTime() {
        return blockGetSession().getCreationTime();
    }

    @Override
    public Instant getLastAccessTime() {
        return blockGetSession().getLastAccessTime();
    }

    @Override
    public void updateLastAccessTime() {
        blockGetSession().updateLastAccessTime();
    }

    @Override
    public void setAttribute(String attributeKey, Object attributeValue) {
        blockGetSession().setAttribute(attributeKey, attributeValue);
    }

    @Override
    public <T> T getAttribute(String name) {
        return blockGetSession().getAttribute(name);
    }

    @Override
    public boolean hasAttribute(String name) {
        return blockGetSession().hasAttribute(name);
    }

    @Override
    public boolean isOpened() {
        return blockGetSession().isOpened();
    }

    @Override
    public boolean isActive() {
        return blockGetSession().isActive();
    }

    @Override
    public void close() {
        blockGetSession().close();
        this.fact = null;
    }

    @Override
    public ChannelFuture write(Object data) {
        return blockGetSession().write(data);
    }

    @Override
    public Integer getPort() {
        return blockGetSession().getPort();
    }

    @Override
    public Channel getChannel() {
        return blockGetSession().getChannel();
    }

    private Session blockGetSession() {
        try {
            return fact.get();
        } catch (InterruptedException | ExecutionException exception) {
            throw new RuntimeException(exception);
        }
    }
}
