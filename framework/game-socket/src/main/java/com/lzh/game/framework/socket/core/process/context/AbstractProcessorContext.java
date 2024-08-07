package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;
import com.lzh.game.framework.socket.core.session.Session;

import java.util.Objects;

/**
 * UnSafe
 *
 * @author zehong.l
 * @since 2024-06-20 17:34
 **/
public abstract class AbstractProcessorContext implements ProcessorContext {

    public AbstractProcessorContext(Processor processor, ProcessorPipeline pipeline) {
        this.processor = processor;
        this.pipeline = pipeline;
    }

    private final ProcessorPipeline pipeline;

    protected Processor processor;

    AbstractProcessorContext prev;

    AbstractProcessorContext next;

    static void invokeReceive(final AbstractProcessorContext next, Session session, Object msg) {
        next.invokeReceive(session, msg);
    }

    public abstract void invokeReceive(Session session, Object msg);

    @Override
    public ProcessorPipeline getPipeline() {
        return pipeline;
    }

    public Processor getProcessor() {
        return processor;
    }

    @Override
    public void fireReceive(Session session, Object msg) {
       var next = findNextContext(this, session, msg);
        if (Objects.nonNull(next.processor) && Objects.nonNull(next.processor.service())) {
            next.processor.service().submit(session, msg, () ->  invokeReceive(next, session, msg));
        } else {
            invokeReceive(next, session, msg);
        }
    }

    static AbstractProcessorContext findNextContext(AbstractProcessorContext context, Session session, Object msg) {
        var next = context.next;
        for (;;) {
            if (next.processor.match(session, msg)) {
                return next;
            }
            next = next.next;
        }
    }
}
