package com.lzh.game.client.model;

import com.lzh.game.client.PackUtils;
import com.lzh.game.client.event.ClientStart;
import com.lzh.game.client.model.hello.RequestHello;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClientGlobalService implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void clientStart(ClientStart clientStart) {
        log.info("Client started..");
        log.info("Send hello world");
        PackUtils.sendMessage(clientStart.getChannel(), -10086, new RequestHello("hello world"));
    }
}
