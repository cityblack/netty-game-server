package com.lzh.game.socket.dispatcher;

import com.lzh.game.common.OptionListener;
import com.lzh.game.socket.dispatcher.action.ActionCenter;

/**
 * Execute Action with filter compose
 */
public interface RequestComposeHandler {

    void handler(ServerExchange exchange, ActionCenter actionCenter, OptionListener<ServerExchange> listener);
}
