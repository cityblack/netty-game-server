package com.lzh.game.start.demo;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Protocol(10086)
public class RequestHello {

    private String content;
}
