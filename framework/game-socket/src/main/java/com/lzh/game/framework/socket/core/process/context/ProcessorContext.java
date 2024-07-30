package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.session.Session;

public interface ProcessorContext {

    void fireReceive(Session session, Object msg);

    ProcessorPipeline getPipeline();
}
