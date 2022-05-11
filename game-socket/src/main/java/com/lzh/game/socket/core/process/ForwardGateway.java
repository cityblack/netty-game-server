package com.lzh.game.socket.core.process;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.*;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.ForwardStrategy;
import com.lzh.game.socket.core.RemoteContext;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gate way
 * Use {@link com.lzh.game.socket.core.FutureAsyncResponse}
 */
@Slf4j
public class ForwardGateway implements Process<GameRequest> {

    private GameTcpClient tcpClient;

    private ForwardStrategy strategy;

    private ExecutorService service;

    public ForwardGateway(GameTcpClient tcpClient, ForwardStrategy strategy) {
        // io
        this(tcpClient, strategy, Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2));
    }

    public ForwardGateway(GameTcpClient tcpClient, ForwardStrategy strategy, ExecutorService service) {
        this.tcpClient = tcpClient;
        this.strategy = strategy;
        this.service = service;
    }

    @Override
    public void process(RemoteContext context, GameRequest command) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                int cmd = command.cmd();
                byte[] bytes = command.byteData();
                command.setSession(context.getSession());

                if (command.type() == Constant.REQUEST_SIGN || command.type() == Constant.ONEWAY_SIGN) {
                    GameRequest request = SocketUtils.createRequest(cmd, bytes);
                    request.setSession(command.getSession());
                    request.setType(command.type());
                    request.setBytes(bytes);
                    request.setCommonKey(Constant.REQUEST_COMMAND_KEY);
                    Session forwardSession = strategy.selected(tcpClient, command);
                    if (command.type() == Constant.ONEWAY_SIGN) {
                        tcpClient.oneWay(forwardSession, request);
                    } else {
                        AsyncResponse<byte[]> result = tcpClient.request(forwardSession, request, byte[].class);

                        result.getResponseFuture()
                                .thenAccept(response -> ForwardGateway.this.doResponse(command, context.getSession(), response));
                    }
                }
            }
        });

    }

    private void doResponse(GameRequest request, Session session, Response response) {
        service.execute(new Runnable() {
            @Override
            public void run() {
                int remoteId = request.remoteId();
                AbstractRemotingCommand command = (AbstractRemotingCommand) response;
                command.setRemoteId(remoteId);
                session.write(response);
            }
        });
    }
}
