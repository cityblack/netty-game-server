package com.lzh.game.framework.gateway;

import com.lzh.game.framework.socket.core.bootstrap.client.FutureAsyncResponse;
import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.process.impl.RequestFuture;
import com.lzh.game.framework.socket.core.protocol.Request;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ServerDemo {


    @Receive
    public HelloWordResponse t1(Request request, RequestData data) {
        log.info("get msg: {}-{}", request.getDefine().getMsgId(), data);
        return new HelloWordResponse("hello world");
    }
}
