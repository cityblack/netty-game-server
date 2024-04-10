package com.lzh.game.socket;

import com.lzh.game.socket.core.AbstractCommand;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.socket.core.session.SessionManage;
import com.lzh.game.socket.core.session.SessionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@ChannelHandler.Sharable
public class GameIoHandler<S extends Session> extends SimpleChannelInboundHandler<AbstractCommand> {

    private MessageHandler messageHandler;

    private SessionManage<S> sessionManage;

    public GameIoHandler(MessageHandler messageHandler, SessionManage<S> sessionManage) {
        this.messageHandler = messageHandler;
        this.sessionManage = sessionManage;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        S session = sessionManage.createSession(ctx.channel());
        SessionUtils.channelBindSession(ctx.channel(), session);
        sessionManage.pushSession(session.getId(), session);
        messageHandler.opened(session);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Session s = getSession(ctx.channel());
        if (Objects.isNull(s)) {
            return;
        }
        messageHandler.close(s);
        sessionManage.removeSession(s.getId());
        SessionUtils.channelUnbindSession(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        messageHandler.exceptionCaught(getSession(ctx.channel()), cause);
        super.exceptionCaught(ctx, cause);
    }

    protected Session getSession(Channel channel) {
        return SessionUtils.channelGetSession(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand msg) {
        messageHandler.messageReceived(getSession(ctx.channel()), msg);
    }
}
