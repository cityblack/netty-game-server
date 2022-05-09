package com.lzh.game.socket.core.process;

import com.lzh.game.common.util.Constant;
import com.lzh.game.socket.GameRequest;
import com.lzh.game.socket.Response;
import com.lzh.game.socket.SocketUtils;
import com.lzh.game.socket.core.AsyncResponse;
import com.lzh.game.socket.core.ForwardStrategy;
import com.lzh.game.socket.core.RemoteContext;
import com.lzh.game.socket.core.bootstrap.GameTcpClient;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

/**
 * Gate way
 */
@Slf4j
public class ForwardGateway implements Process<GameRequest> {

    private GameTcpClient tcpClient;

    private ForwardStrategy strategy;

    @Override
    public void process(RemoteContext context, GameRequest command) {
        int cmd = command.cmd();
        byte[] bytes = command.byteData();

        if (command.type() == Constant.REQUEST_SIGN || command.type() == Constant.ONEWAY_SIGN) {
            GameRequest request = SocketUtils.createRequest(cmd, bytes);
            request.setSession(command.getSession());
            request.setType(command.type());
            request.setBytes(bytes);
            request.setCommonKey(Constant.REQUEST_COMMAND_KEY);
            Session forwardSession = strategy.forwardSession(tcpClient, command);
            if (command.type() == Constant.ONEWAY_SIGN) {
                tcpClient.oneWay(forwardSession, request);
            } else {
                AsyncResponse<Response> result = tcpClient.request(forwardSession, request, Response.class);

                result.getResponseFuture()
                        .thenAccept(response -> ForwardGateway.this.doResponse(context.getSession(), response));
            }
        }
    }

    private void doResponse(Session session, Response response) {
        session.write(response);
    }
}
