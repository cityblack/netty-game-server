package com.lzh.game.socket.exchange.session;


import io.netty.channel.Channel;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Map;

public interface Session {

    String getRemoteAddress();

    /**
     * Return the session id
     * @return
     */
    String getId();

    Channel getChannel();

    Instant getCreationTime();

    Instant getLastAccessTime();

    /**
     * Return a map that holds session attributes.
     */
    Map<String, Object> getAttributes();

    void setAttribute(String attributeKey, Object attributeValue);
    /**
     * Return the session attribute value if present.
     * @param name the attribute name
     * @param <T> the attribute type
     * @return the attribute value
     */
    @SuppressWarnings("unchecked")
    @Nullable
    default <T> T getAttribute(String name) {
        return (T) getAttributes().get(name);
    }

    /**
     * Return the session attribute value or if not present raise an
     * {@link IllegalArgumentException}.
     * @param name the attribute name
     * @param <T> the attribute type
     * @return the attribute value
     */
    @SuppressWarnings("unchecked")
    default <T> T getRequiredAttribute(String name) {
        T value = getAttribute(name);
        Assert.notNull(value, "Required attribute '" + name + "' is missing.");
        return value;
    }

    boolean opened();

    /**
     * close the session
     */
    void close();

    void close(SessionCloseListener<Session> listener);

    void write(Object data);

    Integer getPort();

}
