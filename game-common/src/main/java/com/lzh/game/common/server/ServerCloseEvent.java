package com.lzh.game.common.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerCloseEvent {

    private long timestamp;

    private int port;
}
