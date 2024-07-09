package com.lzh.game.framework.gateway.process;

import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.FutureAsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.GameTcpClient;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.protocol.AbstractCommand;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.utils.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gateway
 * Use {@link FutureAsyncResponse}
 */
@Slf4j
public class ForwardGatewayProcess implements Processor {

    private final GameTcpClient tcpClient;

    private final ForwardSessionSelect strategy;

    private Executor service;

    public ForwardGatewayProcess(GameTcpClient tcpClient, ForwardSessionSelect strategy) {
        // io
        this(tcpClient, strategy, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
    }

    public ForwardGatewayProcess(GameTcpClient tcpClient, ForwardSessionSelect strategy, ExecutorService service) {
        this.tcpClient = tcpClient;
        this.strategy = strategy;
        this.service = new Executor(service);
    }

    public void process(Session session, AbstractCommand command) {
        short msgId = command.getMsgId();
        byte[] bytes = (byte[]) command.getData();
        if (command instanceof Request request) {
            Request r = SocketUtils.createRequest(msgId, bytes, command.getType());
            r.setSession(session);
            r.setType(command.getType());
            r.setData(bytes);
            Session forwardSession = strategy.selected(tcpClient, request);
            if (Objects.isNull(forwardSession)) {
                log.error("select session is null");
                return;
            }
            if (command.getType() == Constant.ONEWAY_SIGN) {
                tcpClient.oneWay(forwardSession, request);
            } else {
                AsyncResponse<byte[]> result = tcpClient.request(forwardSession, request, byte[].class);
                result.getResponseFuture()
                        .thenAccept(response -> ForwardGatewayProcess.this.doResponse(request, request.getSession(), response));
            }
        }

    }

    private void doResponse(Request request, Session session, Response response) {
        int remoteId = request.getRequestId();
        response.setRequestId(remoteId);
        session.write(response);
    }

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        process(session, (AbstractCommand) data);
    }

    @Override
    public boolean match(Session session, Object msg) {
        return msg instanceof AbstractCommand;
    }

    @Override
    public ProcessorExecutorService service() {
        return null;
    }

    private static class Executor implements ProcessorExecutorService {

        private final ExecutorService service;

        private Executor(ExecutorService service) {
            this.service = service;
        }

        @Override
        public void submit(Session session, Object msg, Runnable runnable) {
            service.submit(runnable);
        }
    }
}
