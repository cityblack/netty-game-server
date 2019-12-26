package com.lzh.game.socket.exchange.handle;

import com.lzh.game.socket.dispatcher.DispatcherHandler;
import com.lzh.game.socket.exchange.coder.ExchangeProtocol;
import com.lzh.game.socket.exchange.request.GameRequest;
import com.lzh.game.socket.exchange.request.RequestHeader;
import com.lzh.game.socket.exchange.session.ChannelSessionManage;
import com.lzh.game.socket.exchange.session.Session;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class GameHandler extends SimpleChannelInboundHandler<Object> {

    private final ChannelSessionManage channelSession;

    private final DispatcherHandler dispatcher;

    public GameHandler(DispatcherHandler dispatcher, ChannelSessionManage channelSession) {
        this.dispatcher = dispatcher;
        this.channelSession = channelSession;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = channelSession.createSession(ctx.channel());
        if (log.isInfoEnabled()) {
            log.info("{} connected!", session.getId());
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ExchangeProtocol.Request)) {
            if (log.isWarnEnabled()) {
                log.warn("Invalid exchange request..{}, ip:{}", msg, ctx.channel().remoteAddress());
            }
        } else {
            ExchangeProtocol.Request request = (ExchangeProtocol.Request)msg;
            GameRequest gameRequest = new GameRequest();
            Session session = channelSession.getSession(ctx.channel());
            gameRequest.setSession(session);
            gameRequest.setHeader(RequestHeader.of(request.getHead().getCmd()
                    , request.getHead().getVersion()));
            gameRequest.setData(request.getBody().toByteArray());
            dispatcher.doRequest(gameRequest);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (log.isInfoEnabled()) {
            log.info("{} close connect!"
                    , channelSession.closeSession(ctx.channel()).getId());
        }
    }
}
