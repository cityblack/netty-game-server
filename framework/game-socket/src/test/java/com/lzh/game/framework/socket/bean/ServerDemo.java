package com.lzh.game.framework.socket.bean;

import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.proto.RequestData;
import com.lzh.game.framework.socket.proto.ResponseData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerDemo {

    @Receive
    public ResponseData t1(Request request, Session session, RequestData data) {
        log.info("{}-{}-{}-{}", session.getId(), request.getDefine().getMsgId(), data.getId(), data.getAge());
        return new ResponseData(data);
    }

}
