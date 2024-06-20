package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.process.context.ProcessorPipeline;
import com.lzh.game.framework.socket.core.process.event.ProcessEventType;
import com.lzh.game.framework.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Deal message.
 */
@Slf4j
public class MessageHandlerImpl implements MessageHandler {

    private final ProcessorPipeline pipLine;

    public MessageHandlerImpl(ProcessorPipeline pipLine) {
        this.pipLine = pipLine;
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
        doEvent(session, ProcessEventType.EXCEPTION, throwable);
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
        pipLine.fireReceive(session, data);
    }


    private void doEvent(Session session, ProcessEventType type) {
        doEvent(session, type, null);
    }

    private void doEvent(Session session, ProcessEventType type, Object data) {
        pipLine.fireEvent(session, type, data);
    }
}
