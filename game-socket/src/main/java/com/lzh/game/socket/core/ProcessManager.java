package com.lzh.game.socket.core;

import com.lzh.game.socket.RemotingCmd;

import java.util.HashMap;
import java.util.Map;

public class ProcessManager {

    private final Map<Integer, Process<RemotingCmd>> PROCESS_CONTAIN = new HashMap<>();

    public void registerProcess(int type, Process<RemotingCmd> process) {
        PROCESS_CONTAIN.put(type, process);
    }

    public Process<RemotingCmd> getProcess(int type) {
        return PROCESS_CONTAIN.get(type);
    }
}
