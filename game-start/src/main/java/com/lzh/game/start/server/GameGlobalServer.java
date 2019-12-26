package com.lzh.game.start.server;

import com.lzh.game.common.event.SubscribeListener;
import com.lzh.game.common.server.BeforeServerCloseEvent;
import com.lzh.game.common.server.BeforeServerStartEvent;
import com.lzh.game.framework.server.SystemFiveClockEvent;
import com.lzh.game.socket.autoconfig.GameSocketProperties;
import com.lzh.game.start.log.LoggerUtils;
import com.lzh.game.start.util.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class GameGlobalServer implements SubscribeListener {

    @Autowired
    private GameSocketProperties socketProperties;

    @Subscribe
    public void onServerClose(BeforeServerCloseEvent event) {
        LoggerUtils.close();
    }

    @Subscribe
    public void fiveClockEvent(SystemFiveClockEvent event) {
        log.debug("Event ");
    }

    @Subscribe
    public void onServerStar(BeforeServerStartEvent event) {
        log.info("Do server stared event..");
        SpringContext context = SpringContext.singleTon();
        context.getCommonDataManage().init();
        context.getWorldService().initWorld();
        context.getPlayerService().init();

        log.info("Protocol version:{}", socketProperties.getProtocolVersion());
    }
}
