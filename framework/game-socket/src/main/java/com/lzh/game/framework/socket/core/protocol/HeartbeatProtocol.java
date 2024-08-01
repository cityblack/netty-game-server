package com.lzh.game.framework.socket.core.protocol;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.utils.Constant;
import lombok.Data;

/**
 * @author zehong.l
 * @since 2024-08-01 16:06
 **/
@Protocol(value = Constant.AUTH_PROTOCOL_ID)
@Data
public class HeartbeatProtocol {
}
