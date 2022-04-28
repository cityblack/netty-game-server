package com.lzh.game.socket;

import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class GameIoHandler<S extends Session> extends SimpleChannelInboundHandler<RemotingCmd> {

    private MessageHandler messageHandler;

    private SessionManage<S> sessionManage;

    public GameIoHandler(MessageHandler messageHandler, SessionManage<S> sessionManage) {
        this.messageHandler = messageHandler;
        this.sessionManage = sessionManage;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        S session = sessionManage.createSession(ctx.channel());
        if (log.isInfoEnabled()) {
            log.info("{} connected!", session.getId());
        }
        sessionManage.pushSession(session.getId(), session);
        messageHandler.opened(session);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        S s = getSession(ctx.channel());
        messageHandler.close(s);
        sessionManage.removeSession(s.getId());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        messageHandler.exceptionCaught(getSession(ctx.channel()));
        super.exceptionCaught(ctx, cause);
    }

    protected S getSession(Channel channel) {
        return sessionManage.getSession(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RemotingCmd msg) throws Exception {
        messageHandler.messageReceived(getSession(ctx.channel()), msg);
    }
}
