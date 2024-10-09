package com.lzh.game.start.demo;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Protocol(10087)
public class ResponseHello {

    private String content;

    private int version;
}
