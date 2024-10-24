package com.lzh.game.framework.socket.core.invoke.select;

import com.lzh.game.framework.socket.core.session.Session;

@FunctionalInterface
public interface SessionSelect<T extends SessionSelectContext> {

    /**
     * Select session
     * @param context request context
     * @return may be null
     */
    Session selected(T context);
}
