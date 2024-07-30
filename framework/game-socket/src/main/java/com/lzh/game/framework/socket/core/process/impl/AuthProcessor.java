package com.lzh.game.framework.socket.core.process.impl;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.protocol.AuthProtocol;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.utils.Constant;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zehong.l
 * @since 2024-07-30 17:29
 **/
@Slf4j
public class AuthProcessor implements Processor {

    private String slot;

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        if (!session.hasAttribute(Constant.AUTH_SESSION_KEY)) {
            if (data instanceof AuthProtocol auth) {
                session.close();
            } else {
                log.error("session [{}-{}] 401", session.getId(), session.getRemoteAddress());
            }
        } else {
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
