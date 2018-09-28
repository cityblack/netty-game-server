package com.lzh.netty.socket.dispatcher.action;


import com.lzh.netty.socket.dispatcher.ServerExchange;

/**
 * To execute request mapping
 * {@link com.lzh.netty.socket.annotation.Action}
 * {@link com.lzh.netty.socket.annotation.RequestMapping}
 */
public interface ActionCenter {

    /**
     * @param exchange
     */
    void executeAction(ServerExchange exchange);
}
