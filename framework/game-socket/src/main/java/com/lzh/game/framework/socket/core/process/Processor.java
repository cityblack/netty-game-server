package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.session.Session;

/**
 * Io read data process
 * Request
 * Response
 * forward -- gateway
 *
 */
public interface Processor {

    void process(ProcessorContext context, Session session, Object data);

    boolean match(Session session, Object msg);

    /**
     * executors.
     * @return Using the original executors, if the return value is null
     */
    ProcessorExecutorService service();
}
