package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.event.ProcessEvent;
import com.lzh.game.framework.socket.core.process.event.ProcessEventListen;
import com.lzh.game.framework.socket.core.session.Session;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * UnSafe
 * @author zehong.l
 * @since 2024-06-20 18:22
 **/
public class DefaultProcessorPipeline implements ProcessorPipeline {

    final HeadContext head;
    final EmptyContext tail;
    final Map<ProcessEvent, List<ProcessEventListen>> events;

    public DefaultProcessorPipeline() {
        head = new HeadContext(this);
        tail = new EmptyContext(this);
        events = new ConcurrentHashMap<>((ProcessEvent.values().length * 3) >> 1);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public ProcessorPipeline addFirst(Processor processor) {
        var context = new DefaultProcessorContext(this, processor);
        context.next = head.next;
        context.prev = head;
        head.next = context;
        return this;
    }

    @Override
    public ProcessorPipeline addLast(Processor processor) {
        var context = new DefaultProcessorContext(this, processor);
        context.next = tail;
        context.prev = tail.prev;
        tail.prev = context;
        return this;
    }

    @Override
    public ProcessorPipeline addProcessEventListen(ProcessEvent event, ProcessEventListen eventListen) {
        var list = events.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>());
        list.add(eventListen);
        return this;
    }

    @Override
    public ProcessorPipeline fireReceive(Session session, Object msg) {
        head.fireReceive(session, msg);
        return this;
    }

    @Override
    public ProcessorPipeline fireEvent(ProcessEvent event, Session session, Object o) {
        for (ProcessEventListen listen : events.getOrDefault(event, Collections.emptyList())) {
            listen.event(session, o);
        }
        return this;
    }

    final class HeadContext extends AbstractProcessorContext {

        public HeadContext(ProcessorPipeline pipeline) {
            super(pipeline);
        }

        @Override
        public void invokeReceive(Session session, Object msg) {
            fireReceive(session, msg);
        }
    }

    final class EmptyContext extends AbstractProcessorContext {

        public EmptyContext(ProcessorPipeline pipeline) {
            super(pipeline);
        }

        @Override
        public void invokeReceive(Session session, Object msg) {

        }

    }
}
