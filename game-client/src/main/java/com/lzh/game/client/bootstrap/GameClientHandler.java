package com.lzh.game.client.bootstrap;

import com.lzh.game.client.dispatcher.ResponseDispatcher;
import com.lzh.game.client.support.ExchangeProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@ChannelHandler.Sharable
@Slf4j
public class GameClientHandler extends SimpleChannelInboundHandler<ExchangeProtocol.Response> {

    private final ResponseDispatcher responseDispatcher;

    public GameClientHandler(ResponseDispatcher dispatcher) {
        this.responseDispatcher = dispatcher;
    }

    protected void channelRead0(ChannelHandlerContext ctx, ExchangeProtocol.Response msg) throws Exception {
        if (Objects.nonNull(msg)) {
            responseDispatcher.doResponse(ctx.channel(), msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Connect to server..");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client close ");
        super.channelInactive(ctx);
    }
}
