package com.lzh.game.start.server.event;

import com.lzh.game.framework.utils.time.TimeUtils;
import com.lzh.game.start.server.SystemFiveClockEvent;
import org.greenrobot.eventbus.EventBus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SystemTimeEvent {

    @Scheduled(cron = TimeUtils.FIVE_CLOCK_CRON)
    public void fiveClock() {
        EventBus.getDefault().post(SystemFiveClockEvent.of(System.currentTimeMillis()));
    }
}
