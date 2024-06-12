package com.lzh.game.start.server;

import lombok.AllArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;

@AllArgsConstructor
public class ShutdownSampleHook extends Thread {

    private ContextRefreshedEvent event;

    @Override
    public void run() {
        // 先关闭网关
//        event.getApplicationContext().getBean(GameServer.class).shutDown();
        this.sendBeforeCloseServerEvent();
    }

    private void sendBeforeCloseServerEvent() {
//        ServerCloseEvent message = new ServerCloseEvent();
//        message.setTimestamp(System.currentTimeMillis());
//        EventBus.getDefault().post(message);
    }
}
