package com.lzh.game.framework.socket.core.session;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Main class for access to the {@link Session ,Channel}
 */
public interface SessionManage<T extends Session> extends SessionFactory<T> {

    T getSession(String sessionId);

    void updateLastAccessTime(T session);

    void pushSession(String sessionId, T session);

    boolean contain(String sessionId);

    boolean removeSession(String sessionId);

    default void addListener(SessionEvent event, BiConsumer<T, Object> call) {
        addListener(event, call.getClass().getName(), call);
    }

    /**
     * Session listener
     *
     * @param call call back
     */
    void addListener(SessionEvent event, String key, BiConsumer<T, Object> call);

    void removeListener(SessionEvent event, String key);

    void pushEvent(SessionEvent event, T session, Object data);

    List<T> getAllSession();

    void shutdown();
}
