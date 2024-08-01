package com.lzh.game.framework.socket.core.bootstrap.client;

import com.lzh.game.framework.socket.core.GameSocketProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * @author zehong.l
 * @since 2024-07-03 10:57
 **/
@Getter
@Setter
public class GameClientSocketProperties extends GameSocketProperties {

    private int connectTimeout = 2000;

    private int requestTimeout = 5000;

    private long heartbeatInterval = 30000;
}
