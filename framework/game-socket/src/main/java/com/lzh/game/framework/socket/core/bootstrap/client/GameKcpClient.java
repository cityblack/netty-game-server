package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import io.netty.bootstrap.Bootstrap;

/**
 * @author zehong.l
 * @since 2024-09-30 15:40
 **/
public class GameKcpClient<C extends ClientSocketProperties> extends AbstractClient<C> {

    public GameKcpClient(BootstrapContext<C> context) {
        super(context);
    }

    @Override
    protected void doInit(C properties) {

    }

    @Override
    protected Bootstrap createBootstrap() {
        return null;
    }
}
