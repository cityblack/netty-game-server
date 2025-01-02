package com.lzh.game.framework.socket.core.bootstrap.server;

import lombok.Getter;
import lombok.Setter;

/**
 * @author zehong.l
 * @since 2024-11-22 17:38
 **/
@Getter
@Setter
public class WebSocketProperties {

    private boolean enable = false;

    private String path = "/ws";

    private int maxContentLength = 65536;
}
