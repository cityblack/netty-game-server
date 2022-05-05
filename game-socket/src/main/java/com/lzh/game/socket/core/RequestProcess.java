package com.lzh.game.socket.core;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.GameRequest;

public class RequestProcess implements Process<GameRequest> {

    private RequestHandler requestHandler;

    private RequestProcessPool pool;

    public RequestProcess(RequestHandler requestHandler, RequestProcessPool pool) {
        this.requestHandler = requestHandler;
        this.pool = pool;
    }

    @Override
    public void process(RemoteContext context, GameRequest request) {
        // Request process default use {@link Constant.RESPONSE_COMMAND_KEY} response process
        ServerExchange exchange = new ServerExchangeWrapper(request, Constant.RESPONSE_COMMAND_KEY);
        request.setSession(context.getSession());
        pool.submit(exchange, new ProcessTask(exchange));
    }

    private class ProcessTask implements Runnable {

        private ServerExchange exchange;

        public ProcessTask(ServerExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public void run() {
            requestHandler.handler(exchange);
        }
    }
}
