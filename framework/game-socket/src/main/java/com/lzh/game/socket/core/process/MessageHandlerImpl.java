package com.lzh.game.socket.core.process;

import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Deal message.
 */
@Slf4j
public class MessageHandlerImpl implements MessageHandler {

    private ProcessorManager manager;

    public MessageHandlerImpl(ProcessorManager manager) {
        this.manager = manager;
    }

    @Override
    public void opened(Session session) {
        log.info("session [{}/{}] is connected.", session.getId(), session.getRemoteAddress());
        doEvent(session, ProcessEventType.CONNECT);
    }

    @Override
    public void close(Session session) {
        log.info("session [{}/{}] is close.", session.getId(), session.getRemoteAddress());
        doEvent(session, ProcessEventType.CLOSE);
    }

    @Override
    public void exceptionCaught(Session session, Throwable throwable) {
        log.error("session:[{}] error", session.getRemoteAddress(), throwable);
        doEvent(session, ProcessEventType.EXCEPTION);
    }

    @Override
    public void messageReceived(Session session, Object data) {
        if (data instanceof List<?> list) {
            for (Object o : list) {
                received(session, o);
            }
        } else {
            received(session, data);
        }
    }

    private void received(Session session, Object data) {
        Processor process = manager.getProcess(data.getClass());
        process.process(session, data);
    }


    private void doEvent(Session session, ProcessEventType type) {
        for (ProcessEventListen event : manager.getEventListen(type)) {
            event.event(session);
        }
    }
}
