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

    void process(Session session, Object e, ProcessorContext context);

    boolean match(Object msg);
}
