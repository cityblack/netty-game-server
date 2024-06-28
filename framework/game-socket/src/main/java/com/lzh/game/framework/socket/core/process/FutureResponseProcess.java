package com.lzh.game.framework.socket.core.process;

import com.lzh.game.framework.socket.core.process.context.ProcessorContext;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.core.RequestFuture;
import com.lzh.game.framework.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureResponseProcess implements Processor {

    @Override
    public void process(ProcessorContext context, Session session, Object data) {
        RequestFuture.received((Response) data, false);
    }

    @Override
    public boolean match(Session session, Object msg) {
        return false;
    }

    @Override
    public ProcessorExecutorService service() {
        return null;
    }
}
