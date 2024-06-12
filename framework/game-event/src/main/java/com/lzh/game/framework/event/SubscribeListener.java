package com.lzh.game.framework.event;

import java.util.EventListener;

public interface SubscribeListener extends EventListener {

    default void beforeRegister() {}

    default void afterRegister() {}
}
