package com.lzh.game.common.scoket;

import com.lzh.game.common.scoket.session.Session;
import com.lzh.game.common.scoket.session.SessionManage;

public abstract class AbstractBootstrap<T extends GameSocketProperties> {

    protected T properties;

    protected MessageHandler handler;

    protected SessionManage<Session> sessionManage;

    public AbstractBootstrap(T properties, MessageHandler handler) {
        this.properties = properties;
        this.handler = handler;
        this.init(properties);
    }

    public MessageHandler getHandler() {
        return handler;
    }

    protected abstract void init(T properties);
}
