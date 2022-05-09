package com.lzh.game.client;

import com.lzh.game.client.event.ClientStart;
import com.lzh.game.socket.GameClient;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class AfterSpringContextFresh implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private GameClient gameClient;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        EventBus.getDefault().post(ClientStart.of(gameClient));
    }

}
