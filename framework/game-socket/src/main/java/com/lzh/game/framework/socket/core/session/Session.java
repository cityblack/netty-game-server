package com.lzh.game.framework.socket.core.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.time.Instant;
import java.util.Objects;

public interface Session {

    String getRemoteAddress();

    /**
     * Return the session id
     *
     * @return
     */
    String getId();

    Instant getCreationTime();

    Instant getLastAccessTime();

    void updateLastAccessTime();


    void setAttribute(String attributeKey, Object attributeValue);

    /**
     * Return the session attribute value if present.
     *
     * @param name the attribute name
     * @param <T>  the attribute type
     * @return the attribute value
     */
    <T> T getAttribute(String name);

    boolean hasAttribute(String name);

    boolean isOpened();

    boolean isActive();

    /**
     * close the session
     */
    void close();

    ChannelFuture write(Object data);

    Integer getPort();

    Channel getChannel();
}
