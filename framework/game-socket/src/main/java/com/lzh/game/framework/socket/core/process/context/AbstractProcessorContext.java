package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.process.Processor;
import com.lzh.game.framework.socket.core.process.ProcessorExecutorService;

/**
 * @author zehong.l
 * @since 2024-06-20 17:34
 **/
public abstract class AbstractProcessorContext implements ProcessorContext {

    public AbstractProcessorContext(ProcessorPipeline pipeline) {
        this.pipeline = pipeline;
    }

    private ProcessorPipeline pipeline;

    private ProcessorExecutorService<?> executor;

    AbstractProcessorContext prev;

    AbstractProcessorContext next;

//    protected abstract Processor getProcessor();

    public ProcessorExecutorService<?> getExecutor() {
        return executor;
    }
}
