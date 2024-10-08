package com.lzh.game.framework.gateway.process;

import com.lzh.game.framework.socket.core.bootstrap.client.AsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.FutureAsyncResponse;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClient;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.utils.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Gateway
 * Use {@link FutureAsyncResponse}
 */
@Slf4j
public class ForwardGatewayProcess implements Processor {

    private final GameClient client;

    private final ForwardSessionSelect strategy;

    public ForwardGatewayProcess(GameClient client, ForwardSessionSelect strategy) {
        // io
        this.client = client;
        this.strategy = strategy;
    }

    public void process(Session session, Request command) {
        short msgId = command.getMsgId();
        byte[] bytes = (byte[]) command.getData();
        Request request = SocketUtils.createRequest(msgId, bytes, command.getType());
        request.setSession(session);
        request.setType(command.getType());
        request.setData(bytes);
        request.setBytesBody(true);
        Session forwardSession = strategy.selected(request);
        if (Objects.isNull(forwardSession)) {
            log.error("select session is null");
            return;
        }
        if (command.getType() == Constant.ONEWAY_SIGN) {
            client.oneWay(forwardSession, request);
        } else {
            AsyncResponse<byte[]> result = client.request(forwardSession, request, byte[].class);
            result.getResponseFuture()
                    .thenAccept(response -> ForwardGatewayProcess.this.doResponse(command, request.getSession(), response));
        }

    }

    private void doResponse(Request request, Session session, Response response) {
        int remoteId = request.getRequestId();
        response.setRequestId(remoteId);
        session.write(response);
    }

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        process(session, (Request) data);
    }

    @Override
    public boolean match(Session session, Object msg) {
        return msg instanceof Request;
    }

    @Override
    public ProcessorExecutorService service() {
        return null;
    }
}
