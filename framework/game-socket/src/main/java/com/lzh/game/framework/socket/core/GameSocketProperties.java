package com.lzh.game.framework.socket.core;

import com.lzh.game.framework.socket.core.bootstrap.NettyProperties;
import com.lzh.game.framework.socket.core.protocol.serial.impl.fury.FuryProperties;
import com.lzh.game.framework.socket.utils.Constant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class GameSocketProperties {

    // Set false which custom processor
    private NettyProperties netty = new NettyProperties();

    private FuryProperties fury = new FuryProperties();

    private boolean bodyDateToBytes = false;

    private String[] protocolScanner = {"com.lzh"};

    private int defaultSerializeType = Constant.DEFAULT_SERIAL_SIGN;
}
