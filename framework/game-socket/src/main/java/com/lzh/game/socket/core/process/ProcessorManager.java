package com.lzh.game.socket.core.process;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProcessorManager {

    public ProcessorManager() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>());
    }

    public ProcessorManager(Map<Class<?>, Processor<?>> processContain, Map<ProcessEventType, List<ProcessEventListen>> processEventListen) {
        this.processContain = processContain;
        this.processEventListen = processEventListen;
    }

    private final Map<Class<?>, Processor<?>> processContain;

    private final Map<ProcessEventType, List<ProcessEventListen>> processEventListen;

    public void registerProcessor(Class<?> command, Processor<?> process) {
        processContain.put(command, process);
    }

    public void unRegisterProcessor(Class<?> command) {
        processContain.remove(command);
    }

    /**
     * Get process
     *
     * @return
     */
    public Processor<?> getProcess(Class<?> command) {
        return processContain.get(command);
    }

    public boolean hasProcessor(Class<?> command) {
        return processContain.containsKey(command);
    }

    public void addEventListen(ProcessEventType type, ProcessEventListen event) {
        var list = processEventListen.computeIfAbsent(type, e -> new CopyOnWriteArrayList<>());
        list.add(event);
    }

    public List<ProcessEventListen> getEventListen(ProcessEventType type) {
        return processEventListen.getOrDefault(type, Collections.emptyList());
    }
}
