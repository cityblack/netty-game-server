package com.lzh.game.socket.core;

import com.lzh.game.socket.RemotingCommand;

import java.util.HashMap;
import java.util.Map;

public class ProcessManager {

    private final Map<Integer, Process<? extends RemotingCommand>> PROCESS_CONTAIN = new HashMap<>();

    public void registerProcess(int type, Process<? extends RemotingCommand> process) {
        PROCESS_CONTAIN.put(type, process);
    }

    public Process<RemotingCommand> getProcess(int type) {
        return (Process<RemotingCommand>) PROCESS_CONTAIN.get(type);
    }

    public boolean isEmpty() {
        return PROCESS_CONTAIN.isEmpty();
    }
}
