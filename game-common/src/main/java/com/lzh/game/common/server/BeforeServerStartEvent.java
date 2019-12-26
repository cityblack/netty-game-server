package com.lzh.game.common.server;

import lombok.Data;

@Data
public class BeforeServerStartEvent {

    private int port;

    private long timestamp;
}
