package com.lzh.netty.client;

import com.lzh.netty.client.bootstrap.TcpClient;
import com.lzh.netty.client.event.ClientStart;
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
