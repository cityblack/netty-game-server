package com.lzh.game.socket.core;

import com.lzh.game.socket.MessageHandler;
import com.lzh.game.socket.RemotingCommand;
import com.lzh.game.socket.core.process.Process;
import com.lzh.game.socket.core.process.ProcessManager;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 消息事件处理
 */
@Slf4j
public class MessageHandlerImpl implements MessageHandler {

    private ProcessManager processManager;

    public MessageHandlerImpl(ProcessManager processManager) {
        this.processManager = processManager;
    }

    @Override
    public void opened(Session session) {
        log.info("session [{}/{}] is connected.", session.getId(), session.getRemoteAddress());
    }

    @Override
    public void close(Session session) {
        log.info("session [{}/{}] is close.", session.getId(), session.getRemoteAddress());
    }

    @Override
    public void exceptionCaught(Session session, Throwable throwable) {
        log.error("session:[{}] error", session.getRemoteAddress(), throwable);
    }

    @Override
    public void messageReceived(Session session, Object data) {
        if (data instanceof RemotingCommand) {
            RemotingCommand command = (RemotingCommand) data;
            Process<RemotingCommand> process = processManager.getProcess(command.type());
            if (Objects.isNull(process)) {
                log.warn("Undefined the command key:{}", command.type());
                return;
            }
            RemoteContext context = new RemoteContext();
            context.setSession(session);

            process.process(context, command);
        }
    }
}
