package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.event.ProcessEventListen;
import com.lzh.game.framework.socket.core.process.event.ProcessEventType;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * @author zehong.l
 * @since 2024-06-20 18:22
 **/
public class DefaultProcessorPipeline implements ProcessorPipeline {

    final HeadContext head;
    final EmptyContext tail;

    public DefaultProcessorPipeline() {

        head = new HeadContext(this);
        tail = new EmptyContext(this);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void addFirst(Processor processor) {

    }

    @Override
    public void addLast(Processor processor) {

    }

    @Override
    public void addProcessEventListen(ProcessEventType eventType, ProcessEventListen eventListen) {

    }

    @Override
    public void fireReceive(Session session, Object msg) {

    }

    @Override
    public void fireEvent(Session session, ProcessEventType type, Object o) {

    }


    final class HeadContext extends AbstractProcessorContext {

        public HeadContext(ProcessorPipeline pipeline) {
            super(pipeline);
        }

        @Override
        public void fireChannelRead(Session session, Object msg) {
            head.next.fireChannelRead(session, msg);
        }
    }

    final class EmptyContext extends AbstractProcessorContext {

        public EmptyContext(ProcessorPipeline pipeline) {
            super(pipeline);
        }

        @Override
        public void fireChannelRead(Session session, Object msg) {

        }
    }
}
