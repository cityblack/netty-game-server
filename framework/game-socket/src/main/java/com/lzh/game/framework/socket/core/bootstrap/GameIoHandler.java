package com.lzh.game.framework.socket.core.bootstrap;

import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.protocol.AbstractCommand;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionManage;
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

    private ProcessorPipeline pipeline;

    private SessionManage<Session> sessionManage;

    public GameIoHandler(ProcessorPipeline pipeline, SessionManage<Session> sessionManage) {
        this.pipeline = pipeline;
        this.sessionManage = sessionManage;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        var session = sessionManage.createSession(ctx.channel());
        SessionUtils.channelBindSession(ctx.channel(), session);
        sessionManage.pushSession(session.getId(), session);
        log.info("session [{}/{}] is connected.", session.getId(), session.getRemoteAddress());
        pipeline.fireEvent(ProcessEvent.CONNECT, session);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Session session = getSession(ctx.channel());
        if (Objects.isNull(session)) {
            return;
        }
        log.info("session [{}/{}] is close.", session.getId(), session.getRemoteAddress());
        doEvent(ProcessEvent.CLOSE,session);
        sessionManage.removeSession(session.getId());
        SessionUtils.channelUnbindSession(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        pipeline.fireEvent(ProcessEvent.EXCEPTION, getSession(ctx.channel()), cause);
        super.exceptionCaught(ctx, cause);
    }

    protected Session getSession(Channel channel) {
        return SessionUtils.channelGetSession(channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractCommand msg) {
        pipeline.fireReceive(getSession(ctx.channel()), msg);
    }

    private void doEvent(ProcessEvent type, Session session) {
        pipeline.fireEvent(type, session);
    }
}
