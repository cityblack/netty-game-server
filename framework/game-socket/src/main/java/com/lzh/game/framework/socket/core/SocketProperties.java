package com.lzh.game.framework.socket.core;

import com.lzh.game.framework.socket.core.bootstrap.NettyProperties;
import com.lzh.game.framework.socket.core.protocol.serial.fury.FuryProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SocketProperties {

    // Set false which custom processor
    private NettyProperties netty = new NettyProperties();

    private FuryProperties fury = new FuryProperties();

    private boolean bodyDateToBytes = false;

    private String[] protocolScanner = {"com.lzh"};

    private String authSlot = "lzh";

    private int authErrorCloseLimit = 10;

    private int requestTimeout = 10_000;
}
