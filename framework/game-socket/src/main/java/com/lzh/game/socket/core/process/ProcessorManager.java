package com.lzh.game.socket.core.process;

import java.util.HashMap;
import java.util.Map;

public class ProcessorManager {

    private final Map<Class<?>, Processor<?>> PROCESS_CONTAIN = new HashMap<>();


    public void registerProcess(Class<?> command, Processor<?> process) {
        PROCESS_CONTAIN.put(command, process);
    }

    /**
     * Get process
     * @return
     */
    public Processor<?> getProcess(Class<?> command) {
        return PROCESS_CONTAIN.get(command);
    }

    public boolean hasProcessor(Class<?> command) {
        return PROCESS_CONTAIN.containsKey(command);
    }

    public boolean isEmpty() {
        return PROCESS_CONTAIN.isEmpty();
    }
}
