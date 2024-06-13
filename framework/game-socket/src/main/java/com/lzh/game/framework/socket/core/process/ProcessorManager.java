package com.lzh.game.framework.socket.core.process;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProcessorManager {


//    public ProcessorManager() {
//        this(new CopyOnWriteArrayList<>(), new ConcurrentHashMap<>());
//    }
//
//    public ProcessorManager(List<Processor<?>> processContain, Map<ProcessEventType, List<ProcessEventListen>> processEventListen) {
//        this.processContain = processContain;
//        this.processEventListen = processEventListen;
//    }

    private final List<Processor<?>> processContain;

    private final Map<ProcessEventType, List<ProcessEventListen>> processEventListen;

    public void addProcessor(Processor<?> process) {
//        processContain.put(command, process);
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
