package com.lzh.netty.socket.protocol.handle;

import com.lzh.netty.socket.dispatcher.DispatcherHandler;
import com.lzh.netty.socket.protocol.GameRequestEntity;
import static com.lzh.netty.socket.protocol.coder.AbstractRequest.Request;
import com.lzh.netty.socket.protocol.session.ChannelSession;
import com.lzh.netty.socket.protocol.session.Session;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ChannelHandler.Sharable
public class GameHandler extends SimpleChannelInboundHandler<Object> {

    private final ChannelSession channelSession;

    private final DispatcherHandler dispatcher;

    public GameHandler(DispatcherHandler dispatcher, ChannelSession channelSession) {
        this.dispatcher = dispatcher;
        this.channelSession = channelSession;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = channelSession.createSession(ctx.channel());
        log.info("{} connected!", session.getId());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Request) && msg == null) {
            log.debug("invalid protocol request..");
        } else {
            Request request = (Request)msg;
            GameRequestEntity gameRequest = new GameRequestEntity();
            Session session = channelSession.getSession(ctx.channel());
            gameRequest.setSession(session);
            gameRequest.setDataString(request.getData());
            gameRequest.setProtocolId(request.getProtocolId());
            gameRequest.setVersion(request.getVersion());
            dispatcher.doRequest(gameRequest);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("{} close connect!"
                , channelSession.closeSession(ctx.channel()).getId());
    }
}
