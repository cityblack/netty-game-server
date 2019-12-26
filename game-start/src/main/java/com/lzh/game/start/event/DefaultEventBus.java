package com.lzh.game.start.event;

import com.lzh.game.common.event.Event;
import com.lzh.game.common.event.EventBus;
import org.springframework.stereotype.Component;

@Component
public class DefaultEventBus implements EventBus {

    @Override
    public void post(Event event) {
        org.greenrobot.eventbus.EventBus.getDefault().post(event);
    }

    @Override
    public void globalPost(Event event) {
        org.greenrobot.eventbus.EventBus.getDefault().post(event);
    }
}
