package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.process.event.ProcessEventListen;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * @author zehong.l
 * @since 2024-06-20 17:40
 **/
public interface ProcessorPipeline {

    ProcessorPipeline addFirst(Processor processor);

    ProcessorPipeline addLast(Processor processor);

    ProcessorPipeline addProcessEventListen(ProcessEvent event, ProcessEventListen eventListen);

    ProcessorPipeline fireReceive(Session session, Object msg);

    ProcessorPipeline fireEvent(ProcessEvent event, Session session, Object o);

    default ProcessorPipeline fireEvent(ProcessEvent event, Session session) {
        return fireEvent(event, session, null);
    }
}
