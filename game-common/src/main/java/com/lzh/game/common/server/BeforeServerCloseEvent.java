package com.lzh.game.common.server;

import lombok.Data;

@Data
public class BeforeServerCloseEvent {

    private int port;

    private long timestamp;

}
