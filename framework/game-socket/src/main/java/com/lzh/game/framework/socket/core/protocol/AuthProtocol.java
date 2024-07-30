package com.lzh.game.framework.socket.core.protocol;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.framework.socket.utils.Constant;
import lombok.Data;

/**
 * @author zehong.l
 * @since 2024-07-30 17:48
 **/
@Protocol(value = Constant.AUTH_PROTOCOL_ID)
@Data
public class AuthProtocol {

    private String slot;

}
