package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * @author zehong.l
 * @since 2024-06-21 16:51
 **/
public class DefaultProcessorContext extends AbstractProcessorContext {

    private final Processor processor;

    public DefaultProcessorContext(ProcessorPipeline pipeline, Processor processor) {
        super(pipeline);
        this.processor = processor;
    }

    @Override
    public void invokeReceive(Session session, Object msg) {
        processor.process(this, session, msg);
    }
}
