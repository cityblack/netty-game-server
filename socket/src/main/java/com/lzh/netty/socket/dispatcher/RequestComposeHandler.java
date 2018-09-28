package com.lzh.netty.socket.dispatcher;

import com.lzh.netty.socket.dispatcher.action.ActionCenter;

/**
 * Execute Action with filter compose
 */
public interface RequestComposeHandler {

    void handler(ServerExchange exchange, ActionCenter actionCenter);
}
