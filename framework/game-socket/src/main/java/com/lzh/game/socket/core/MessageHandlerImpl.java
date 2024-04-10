package com.lzh.game.socket.core;

import com.lzh.game.socket.MessageHandler;
import com.lzh.game.socket.core.process.Processor;
import com.lzh.game.socket.core.process.ProcessorManager;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 消息事件处理
 */
@Slf4j
public class MessageHandlerImpl implements MessageHandler {

    private ProcessorManager manager;

    @Override
    public void opened(Session session) {
        log.info("session [{}/{}] is connected.", session.getId(), session.getRemoteAddress());
    }

    @Override
    public void close(Session session) {
        log.info("session [{}/{}] is close.", session.getId(), session.getRemoteAddress());
    }

    @Override
    public void exceptionCaught(Session session, Throwable throwable) {
        log.error("session:[{}] error", session.getRemoteAddress(), throwable);
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
}
