package com.lzh.game.start.server;

import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.common.event.SubscribeListener;
import com.lzh.game.common.server.ServerCloseEvent;
import com.lzh.game.common.server.ServerStartEvent;
import com.lzh.game.framework.server.SystemFiveClockEvent;
import com.lzh.game.socket.config.GameServerSocketProperties;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.log.LoggerUtils;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class GameGlobalServer implements SubscribeListener {

    @Resource
    private GameServerSocketProperties gameServerSocketProperties;

    @Subscribe
    public void onServerClose(ServerCloseEvent event) {
        LoggerUtils.close();
    }

    @Subscribe
    public void fiveClockEvent(SystemFiveClockEvent event) {
        log.debug("Event five");
    }

    @Subscribe
    public void onServerStar(ServerStartEvent event) {
        log.info("Do server stared event..");
        try {
            doServerStar();
        } catch (Exception e) {
            log.error("", e);
            System.exit(-1);
        }

        log.info("Protocol version:{}", gameServerSocketProperties.getProtocolVersion());
    }

    public void doServerStar() {
        LogReason.check();
        ApplicationUtils.getBeansOfType(AfterServerStartInit.class)
                .values()
                .stream()
                .sorted()
                .forEach(e -> e.init());

    }
}
