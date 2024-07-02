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
     * Use current thread while the value is null
     * @return Using the original executors.
     */
    ProcessorExecutorService service();
}
