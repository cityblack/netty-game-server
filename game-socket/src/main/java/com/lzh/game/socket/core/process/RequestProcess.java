package com.lzh.game.socket.core.process;

import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.core.*;

public class RequestProcess implements Process<GameRequest> {

    private RequestHandle requestHandle;

    private RequestProcessPool pool;

    public RequestProcess(RequestHandle requestHandle, RequestProcessPool pool) {
        this.requestHandle = requestHandle;
        this.pool = pool;
    }

    @Override
    public void process(RemoteContext context, GameRequest request) {
        ServerExchange exchange = new ServerExchangeWrapper(request);
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
            requestHandle.handle(exchange);
        }
    }
}
