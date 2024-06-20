package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.event.ProcessEventListen;
import com.lzh.game.framework.socket.core.process.event.ProcessEventType;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * @author zehong.l
 * @since 2024-06-20 17:40
 **/
public interface ProcessorPipeline {

    void addFirst(Processor processor);

    void addLast(Processor processor);

    void addProcessEventListen(ProcessEventType eventType, ProcessEventListen eventListen);

    void fireReceive(Session session, Object msg);

    void fireEvent(Session session, ProcessEventType type, Object o);
}
