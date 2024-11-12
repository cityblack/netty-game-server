package com.lzh.game.framework.gateway.select.factory;

import com.lzh.game.framework.gateway.select.SessionSelect;
import com.lzh.game.framework.socket.core.bootstrap.client.AbstractClient;

/**
 * @author zehong.l
 * @since 2024-10-24 10:42
 **/
public interface SessionSelectFactory {

    SessionSelect createSessionSelect(AbstractClient<?> client);
}
