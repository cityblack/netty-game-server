package com.lzh.game.framework.socket.core.bootstrap.handler;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.protocol.AbstractCommand;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionEvent;
import com.lzh.game.framework.socket.core.session.SessionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@ChannelHandler.Sharable
public class GameIoHandler extends SimpleChannelInboundHandler<AbstractCommand> {

    private final BootstrapContext<?> context;

    public GameIoHandler(BootstrapContext<?> context) {
        this.context = context;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        var sessionManage = context.getSessionManage();
        var session = sessionManage.createSession(ctx.channel(), context);
        sessionManage.pushSession(session.getId(), session);
        log.info("session [{}/{}] is connected.", session.getId(), session.getRemoteAddressStr());

        SessionUtils.channelBindSession(ctx.channel(), session);
        doEvent(ProcessEvent.CONNECT, session);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Session session = getSession(ctx.channel());
        if (Objects.isNull(session)) {
            return;
        }
        log.info("session [{}/{}] is close.", session.getId(), session.getRemoteAddressStr());
        doEvent(ProcessEvent.CLOSE, session);
        context.getSessionManage().removeSession(session.getId());
        SessionUtils.channelUnbindSession(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        context.getPipeline().fireEvent(ProcessEvent.EXCEPTION, getSession(ctx.channel()), cause);
        log.error("", cause);
        Session session = getSession(ctx.channel());
        context.getSessionManage().pushEvent(SessionEvent.ERROR, session, cause);
        super.exceptionCaught(ctx, cause);
    }

    protected Session getSession(Channel channel) {
        return SessionUtils.channelGetSession(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand msg) {
        context.getPipeline().fireReceive(getSession(ctx.channel()), msg);
    }

    private void doEvent(ProcessEvent event, Session session) {
        context.getPipeline().fireEvent(event, session);
    }
}
