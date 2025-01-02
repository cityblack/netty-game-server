package com.lzh.game.framework.socket.core.session.impl;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.exchange.ProtoRequest;
import com.lzh.game.framework.socket.core.exchange.SessionProtoRequest;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.VoidChannelPromise;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

@Slf4j
public abstract class AbstractSession implements Session {

    private final transient Channel channel;
    private String id;
    private String remoteAddress;
    private transient Instant lastAccessTime;
    private transient Instant creationTime;
    private Integer port;
    private ProtoRequest request;

    protected AbstractSession(Channel channel, BootstrapContext<?> context) {
        this(channel, true, context);
    }

    protected AbstractSession(Channel channel, boolean init, BootstrapContext<?> context) {
        this.channel = channel;
        if (init) {
            this.init(channel);
        }
        if (Objects.nonNull(context)) {
            this.request = new SessionProtoRequest(this, context);
        }
    }

    protected void init(Channel channel) {
        this.remoteAddress = channel.remoteAddress().toString().replace("/", "");
        String[] split = this.remoteAddress.split(":");
        if (split.length > 1) {
            this.port = Integer.valueOf(split[1]);
        }
        this.id = channel.id().asLongText();
        setCreationTime();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public String getRemoteAddressStr() {
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
        return write(data, false);
    }

    @Override
    public ChannelFuture write(Object data, boolean closeFuture) {
        if (log.isDebugEnabled() && data instanceof Request re) {
            log.debug("[{}] send meg id:{}", getId(), re.getMsgId());
        }
        if (closeFuture) {
            return channel.writeAndFlush(data);
        }
        return channel.writeAndFlush(data, new VoidChannelPromise(channel, true));

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

    @Override
    public void oneWay(Object param) {
        request.oneWay(param);
    }

    @Override
    public <T> AsyncResponse<T> request(Object param, Class<T> returnType, ExecutorService service) {
        return request.request(param, returnType, service);
    }
}
