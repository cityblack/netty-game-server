package com.lzh.game.framework.socket;

import com.lzh.game.framework.socket.core.bootstrap.NettyProperties;
import com.lzh.game.framework.socket.core.protocol.serial.impl.fury.FuryProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GameSocketProperties {

    // Set false which custom processor
    private boolean useDefaultProcessor = true;

    private NettyProperties netty = new NettyProperties();

    private FuryProperties fury = new FuryProperties();
}
