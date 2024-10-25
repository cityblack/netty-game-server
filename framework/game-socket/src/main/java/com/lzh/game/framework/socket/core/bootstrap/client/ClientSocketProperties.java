package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.SocketProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * @author zehong.l
 * @since 2024-07-03 10:57
 **/
@Getter
@Setter
public class ClientSocketProperties extends SocketProperties {

    private int connectTimeout = 2000;

    private long clientIdleTime = TimeUnit.MINUTES.toMillis(1);

    // Heartbeat interval time. default 30 second
    private int heartbeatInterval = 1_000 * 30;
}
