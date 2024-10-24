package com.lzh.game.framework.socket.core.process.impl;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.protocol.AuthProtocol;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.core.session.SessionEvent;
import com.lzh.game.framework.socket.utils.Constant;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * After connection, it is necessary to verify through this class before proceeding with subsequent communication
 * io read data -> context.head -> AuthProcessor
 *
 * @author zehong.l
 * @since 2024-07-30 17:29
 **/
@Slf4j
public class AuthProcessor implements Processor {

    private final BootstrapContext<?> context;

    public AuthProcessor(BootstrapContext<?> context) {
        this.context = context;
    }

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        Request request = (Request) data;
        if (request.getMsgId() == Constant.AUTH_PROTOCOL_ID) {
            var auth = (AuthProtocol) request.getData();
            if (auth.verify(this.context.getProperties().getAuthSlot())) {
                log.info("connect [{}] verification successful", session.getId());
                session.setAttribute(Constant.AUTH_SESSION_KEY, true);
            }
            this.context.getSessionManage().pushEvent(SessionEvent.AUTH, session, null);
            return;
        }
        if (!session.hasAttribute(Constant.AUTH_SESSION_KEY)) {
            log.error("session [{}-{}-{}] 401", session.getId(), session.getRemoteAddressStr(), data.getClass());
            Integer time = session.getAttribute(Constant.AUTH_SESSION_KEY);

            if (Objects.nonNull(time) && time >= this.context.getProperties().getAuthErrorCloseLimit()) {
                session.close();
            } else {
                session.setAttribute(Constant.AUTH_SESSION_KEY, Objects.isNull(time) ? 1 : time + 1);
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
