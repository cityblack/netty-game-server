package com.lzh.game.framework.client.model;

import com.lzh.game.framework.client.PackUtils;
import com.lzh.game.framework.client.event.ClientStart;
import com.lzh.game.framework.client.model.hello.RequestHello;
import com.lzh.game.framework.socket.core.bootstrap.client.GameClient;
import com.lzh.game.framework.socket.core.session.Session;
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
        GameClient client = clientStart.getClient();
        Session session = client.conn("localhost", 8089, 2000);
        log.info("Send hello world");
        PackUtils.sendMessage(session.getChannel(), -10086, new RequestHello("hello world"));
    }
}
