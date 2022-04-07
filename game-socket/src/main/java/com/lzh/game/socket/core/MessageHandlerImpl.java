package com.lzh.game.socket.core;

import com.lzh.game.common.scoket.GameRequest;
import com.lzh.game.common.scoket.MessageHandler;
import com.lzh.game.common.scoket.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 消息事件处理
 */
@Slf4j
public class MessageHandlerImpl implements MessageHandler {

    private RequestHandler requestHandler;

    private RequestBusinessPool pool;

    public MessageHandlerImpl(RequestHandler requestHandler, RequestBusinessPool pool) {
        this.requestHandler = requestHandler;
        this.pool = pool;
    }

    @Override
    public void opened(Session session) {
        pool.submit(session, new IoOpenTask());
    }

    @Override
    public void close(Session session) {
        pool.submit(session, new IoCloseTask());
    }

    @Override
    public void exceptionCaught(Session session) {

    }

    @Override
    public void messageReceived(Session session, Object data) {
        ServerExchange exchange = createExchange(session, data);
        if (Objects.isNull(exchange)) {
            return;
        }
        pool.submit(exchange, new IoReceivedTask(requestHandler, exchange));
    }

    public static class IoCloseTask implements Runnable {

        @Override
        public void run() {

        }
    }

    public static class IoOpenTask implements Runnable {

        @Override
        public void run() {

        }
    }

    public static class IoReceivedTask implements Runnable {

        private RequestHandler handler;

        private ServerExchange exchange;

        public IoReceivedTask(RequestHandler handler, ServerExchange exchange) {
            this.handler = handler;
            this.exchange = exchange;
        }

        @Override
        public void run() {
            handler.handler(exchange);
        }
    }

    private ServerExchange createExchange(Session session, Object data) {
        if (!(data instanceof GameRequest)) {
            if (log.isWarnEnabled()) {
                log.warn("Invalid core request..{}, ip:{}", data, session.getChannel().remoteAddress());
            }
            return null;
        } else {
            GameRequest request = (GameRequest) data;
            request.setSession(session);
            return new ServerExchangeWrapper(request);
        }
    }
}
