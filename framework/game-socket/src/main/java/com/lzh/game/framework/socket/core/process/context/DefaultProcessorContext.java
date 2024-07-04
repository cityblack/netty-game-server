package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * @author zehong.l
 * @since 2024-06-21 16:51
 **/
public class DefaultProcessorContext extends AbstractProcessorContext {

    public DefaultProcessorContext(ProcessorPipeline pipeline, Processor processor) {
        super(processor, pipeline);
    }

    @Override
    public void invokeReceive(Session session, Object msg) {
        getProcessor().process(this, session, msg);
    }
}
