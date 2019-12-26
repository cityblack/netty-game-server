package com.lzh.game.framework.server.event;

import com.lzh.game.framework.server.SystemFiveClockEvent;
import org.greenrobot.eventbus.EventBus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemTimeEvent {

    @Scheduled(cron = "0 0 5 * * ?")
    public void fiveClock() {
        EventBus.getDefault().post(SystemFiveClockEvent.of(System.currentTimeMillis()));
    }
}
