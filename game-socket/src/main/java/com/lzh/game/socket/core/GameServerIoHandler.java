package com.lzh.game.socket.core;

import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class GameServerIoHandler extends SimpleChannelInboundHandler<Object> {

    private MessageHandler messageHandler;

    private SessionManage<Session> sessionManage;

    public GameServerIoHandler(MessageHandler messageHandler, SessionManage<Session> sessionManage) {
        this.messageHandler = messageHandler;
        this.sessionManage = sessionManage;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Session session = sessionManage.createSession(ctx.channel());
        if (log.isInfoEnabled()) {
            log.info("{} connected!", session.getId());
        }
        sessionManage.pushSession(session.getId(), session);
        messageHandler.opened(session);
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        messageHandler.messageReceived(getSession(ctx.channel()), msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        messageHandler.close(getSession(ctx.channel()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        messageHandler.exceptionCaught(getSession(ctx.channel()));
        super.exceptionCaught(ctx, cause);
    }

    public Session getSession(Channel channel) {
        return sessionManage.getSession(channel);
    }
}
