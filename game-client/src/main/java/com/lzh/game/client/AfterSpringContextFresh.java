package com.lzh.game.client;

import com.lzh.game.client.event.ClientStart;
import com.lzh.game.client.bootstrap.TcpClient;
import org.greenrobot.eventbus.EventBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class AfterSpringContextFresh implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ClientProperties properties;

    @Autowired
    private TcpClient tcpClient;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        tcpClient.conn(properties.getHost(), properties.getPort());
        EventBus.getDefault().post(ClientStart.of(tcpClient.getChannel()));
    }

}
