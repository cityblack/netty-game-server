package com.lzh.game.start.gm.packet;

import com.lzh.game.framework.socket.core.protocol.message.Protocol;
import com.lzh.game.start.util.Constant;
import lombok.Data;

@Data
@Protocol(Constant.GM_MSG_ID)
public class GmParam {

    private String methodName;

    private String value;
}
