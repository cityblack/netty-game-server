package com.lzh.game.framework.socket.core.process.context;

import com.lzh.game.framework.socket.core.session.Session;

public interface ProcessorContext {

    void fireChannelRead(Session session, Object msg);
//    private final Map<ProcessEventType, List<ProcessEventListen>> processEventListen;
//
//    public ProcessorContext(Map<ProcessEventType, List<ProcessEventListen>> processEventListen) {
//        this.processEventListen = processEventListen;
//    }
//
//
//
//    public void addEventListen(ProcessEventType type, ProcessEventListen event) {
//        var list = processEventListen.computeIfAbsent(type, e -> new CopyOnWriteArrayList<>());
//        list.add(event);
//    }
//
//    public List<ProcessEventListen> getEventListen(ProcessEventType type) {
//        return processEventListen.getOrDefault(type, Collections.emptyList());
//    }


}
