package com.lzh.game.framework.socket.bean;

import com.lzh.game.framework.socket.core.invoke.Receive;
import com.lzh.game.framework.socket.core.protocol.Request;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zehong.l
 * @since 2024-06-28 18:02
 **/
@Slf4j
public class RequestTestBean {

    @Receive
    public void t1(Request request, RequestData data) {
        log.info("{}-{}-{}-{}", request.getSession().getId(), request.getDefine().getMsgId(), data.getId(), data.getAge());
    }

    @Receive(10087)
    public void t2(Request request, int type, RequestData data) {
        log.info("{}-{}-{}-{}-{}", request.getSession().getId(), request.getDefine().getMsgId()
                , type, data.getId(), data.getAge());
    }
}
