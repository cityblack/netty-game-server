package com.lzh.game.framework.socket.core.bootstrap.handler;

import com.lzh.game.framework.socket.core.protocol.HeartbeatProtocol;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-09-30 10:49
 **/
@ChannelHandler.Sharable
public class ClientIdleHandler extends ChannelDuplexHandler {

    private static final HeartbeatProtocol HEARTBEAT = new HeartbeatProtocol();

    private static final AttributeKey<Integer> COUNT = AttributeKey.valueOf("IDLE_ACTIVE_COUNT");

    private final ChannelFutureListener writeListener = future -> {
        var ch = future.channel();
        ch.attr(COUNT).set(0);
    };

    private final int heartFailCloseTimes;

    public ClientIdleHandler(int heartFailCloseTimes) {
        this.heartFailCloseTimes = heartFailCloseTimes;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent stateEvent) {
            if (stateEvent.state() == IdleState.WRITER_IDLE) {
                var count = getChannelIdleTimes(ctx.channel());
                // server lose response.
                if (count >= heartFailCloseTimes) {
                    ctx.channel().close();
                } else {
                    ctx.write(HEARTBEAT).addListener(writeListener);
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    private int getChannelIdleTimes(Channel channel) {
        var count = channel.attr(COUNT);
        return Objects.isNull(count) ? 0 : count.get();
    }
}
