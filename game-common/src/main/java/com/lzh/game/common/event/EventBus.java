package com.lzh.game.common.event;

public interface EventBus {
    /**
     * 只在该服务器
     * @param event
     */
    void post(Event event);

    /**
     * 全局通知 跨服务器
     * @param event
     */
    void globalPost(Event event);
}
