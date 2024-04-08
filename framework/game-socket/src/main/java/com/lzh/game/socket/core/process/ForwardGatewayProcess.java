package com.lzh.game.socket.core.process;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.*;
import com.lzh.game.socket.core.AbstractRemotingCommand;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.ForwardSessionSelect;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gateway
 * Use {@link com.lzh.game.socket.core.FutureAsyncResponse}
 */
@Slf4j
public class ForwardGatewayProcess implements Process<Request> {

    private GameTcpClient tcpClient;

    private ForwardSessionSelect strategy;

    private ExecutorService service;

    public ForwardGatewayProcess(GameTcpClient tcpClient, ForwardSessionSelect strategy) {
        // io
        this(tcpClient, strategy, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
    }

    public ForwardGatewayProcess(GameTcpClient tcpClient, ForwardSessionSelect strategy, ExecutorService service) {
        this.tcpClient = tcpClient;
        this.strategy = strategy;
        this.service = service;
    }

    @Override
    public void process(Request command) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                int msgId = command.getMsgId();
                byte[] bytes = (byte[]) command.getData();

                if (Constant.IS_REQUEST_SIGN(command.getType())) {
                    Request request = SocketUtils.createRequest(msgId, bytes, command.getType());
                    request.setSession(command.getSession());
                    request.setType(command.getType());
                    request.setData(bytes);
                    Session forwardSession = strategy.selected(tcpClient, command);
                    if (Objects.isNull(forwardSession)) {
                        log.error("select session is null");
                        return;
                    }
                    if (command.getType() == Constant.ONEWAY_SIGN) {
                        tcpClient.oneWay(forwardSession, request);
                    } else {
                        AsyncResponse<byte[]> result = tcpClient.request(forwardSession, request, byte[].class);

                        result.getResponseFuture()
                                .thenAccept(response -> ForwardGatewayProcess.this.doResponse(command, request.getSession(), response));
                    }
                }
            }
        });

    }

    private void doResponse(Request request, Session session, Response response) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                int remoteId = request.getRequestId();
                response.setRequestId(remoteId);
                session.write(response);
            }
        });
    }
}
