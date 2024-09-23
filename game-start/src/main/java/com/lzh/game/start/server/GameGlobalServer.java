package com.lzh.game.start.server;

import com.lzh.game.framework.event.SubscribeListener;
import com.lzh.game.start.util.ApplicationUtils;
import com.lzh.game.framework.socket.core.bootstrap.server.GameServerSocketProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class GameGlobalServer implements SubscribeListener {

    @Resource
    private GameServerSocketProperties gameServerSocketProperties;


    @Subscribe
    public void fiveClockEvent(SystemFiveClockEvent event) {
        log.debug("Event five");
    }

//    @Subscribe
//    public void onServerStar(ServerStartEvent event) {
//        log.info("Do server stared event..");
//        try {
//            doServerStar();
//        } catch (Exception e) {
//            log.error("", e);
//            System.exit(-1);
//        }
//
//        log.info("Protocol version:{}", gameServerSocketProperties.getProtocolVersion());
//    }

    public void doServerStar() {
        ApplicationUtils.getBeansOfType(AfterServerStartInit.class)
                .values()
                .stream()
                .sorted()
                .forEach(e -> e.init());

    }
}
