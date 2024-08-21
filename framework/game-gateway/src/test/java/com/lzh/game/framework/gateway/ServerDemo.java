package com.lzh.game.framework.gateway;

import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.invoke.convert.SysParam;
import com.lzh.game.framework.socket.core.protocol.Request;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerDemo {


    @Receive
    public void t1(@SysParam Request request, RequestData data) {
        log.info("{}-{}-{}-{}", request.getSession().getId(), request.getDefine().getMsgId(), data.getId(), data.getAge());
    }


}
