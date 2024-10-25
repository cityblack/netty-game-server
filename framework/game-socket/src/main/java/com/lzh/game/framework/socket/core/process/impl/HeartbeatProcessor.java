package com.lzh.game.framework.socket.core.process.impl;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.Constant;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zehong.l
 * @since 2024-08-01 17:15
 **/
@Slf4j
public class HeartbeatProcessor implements Processor {

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        Request request = (Request) data;
        if (request.getMsgId() != Constant.HEARTBEAT_PROTOCOL_ID) {
            context.fireReceive(session, data);
        }
    }

    @Override
    public boolean match(Session session, Object msg) {
        return msg instanceof Request;
    }

    @Override
    public ProcessorExecutorService service() {
        return null;
    }
}
