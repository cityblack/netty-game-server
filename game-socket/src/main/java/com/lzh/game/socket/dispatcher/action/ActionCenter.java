package com.lzh.game.socket.dispatcher.action;


import com.lzh.game.common.OptionListener;
import com.lzh.game.socket.dispatcher.ServerExchange;

/**
 * To execute request mapping
 * {@link com.lzh.game.socket.annotation.Action}
 * {@link com.lzh.game.socket.annotation.RequestMapping}
 */
public interface ActionCenter {

    /**
     * parse request data {@link ServerExchange#getRequest()} and
     * reach the result to set response {@link ServerExchange#getResponse()}
     * @param exchange
     */
    void executeAction(ServerExchange exchange, OptionListener<ServerExchange> listener);
}
