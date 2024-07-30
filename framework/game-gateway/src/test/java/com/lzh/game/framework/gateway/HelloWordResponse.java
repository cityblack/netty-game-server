package com.lzh.game.framework.gateway;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zehong.l
 * @since 2024-07-30 12:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Protocol(-1003)
public class HelloWordResponse {

    private String content;
}
