package com.lzh.game.framework.socket.core.invoke.select.factory;

import com.lzh.game.framework.socket.core.bootstrap.client.AbstractClient;
import com.lzh.game.framework.socket.core.invoke.select.SessionSelect;
import com.lzh.game.framework.socket.core.invoke.select.SessionSelectContext;

/**
 * @author zehong.l
 * @since 2024-10-24 10:42
 **/
public interface SessionSelectFactory {

    SessionSelect<? extends SessionSelectContext> createSessionSelect(AbstractClient<?> client);
}
