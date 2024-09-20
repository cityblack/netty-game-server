package com.lzh.game.framework.socket.proto;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zehong.l
 * @since 2024-08-21 17:56
 **/
@AllArgsConstructor
@Data
@Protocol(-10087)
@NoArgsConstructor
public class ResponseData {

    private RequestData data;
}
