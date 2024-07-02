package com.lzh.game.framework.client;

import com.lzh.game.framework.client.event.ClientStart;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClient;
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
