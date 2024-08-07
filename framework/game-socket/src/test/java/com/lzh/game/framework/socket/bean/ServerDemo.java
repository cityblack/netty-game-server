package com.lzh.game.framework.socket.bean;

import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.invoke.convert.SysParam;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.proto.RequestData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerDemo {

    @Receive(-1000)
    public String hello(String hello) {
        log.info(hello);
        return "server say:" + hello;
    }

    @Receive(-1002)
    public void noeWay(String one) {
        log.info(one);
    }

    @Receive
    public void t1(@SysParam Request request, RequestData data) {
        log.info("{}-{}-{}-{}", request.getSession().getId(), request.getDefine().getMsgId(), data.getId(), data.getAge());
    }

    @Receive(1004)
    public void t2(@SysParam Request request, int type, RequestData data) {
        log.info("{}-{}-{}-{}-{}", request.getSession().getId(), request.getDefine().getMsgId()
                , type, data.getId(), data.getAge());
    }


}
