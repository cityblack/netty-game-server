package com.lzh.game.socket.core;

import com.lzh.game.socket.MessageHandler;
import com.lzh.game.socket.RemotingCmd;
import com.lzh.game.socket.core.session.Session;
import lombok.extern.slf4j.Slf4j;

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

    }

    @Override
    public void close(Session session) {

    }

    @Override
    public void exceptionCaught(Session session) {

    }

    @Override
    public void messageReceived(Session session, Object data) {
        if (data instanceof RemotingCmd) {
            RemotingCmd command = (RemotingCmd) data;
            Process<RemotingCmd> process = processManager.getProcess(command.commandKey());
            process.process(command);
        }
    }
}
