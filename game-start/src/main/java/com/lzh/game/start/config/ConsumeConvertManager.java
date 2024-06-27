package com.lzh.game.start.config;

import com.lzh.game.framework.socket.core.invoke.convert.RequestConvert;
import com.lzh.game.framework.socket.core.invoke.convert.RequestConvertManager;
import com.lzh.game.start.config.convert.PlayerConvert;
import com.lzh.game.start.model.player.Player;

public class ConsumeConvertManager implements RequestConvertManager {

    private RequestConvertManager fact;

    public ConsumeConvertManager(RequestConvertManager fact) {
        this.fact = fact;
        registerInner();
    }

    private void registerInner() {
        this.registerConvert(Player.class, new PlayerConvert());
    }

    @Override
    public void registerConvert(Class<?> target, RequestConvert<?> convert) {
        this.fact.registerConvert(target, convert);
    }

    @Override
    public RequestConvert<?> getConvert(Class<?> clazz) {
        return fact.getConvert(clazz);
    }

    @Override
    public boolean hasConvert(Class<?> target) {
        return fact.hasConvert(target);
    }

    @Override
    public RequestConvert<?> getOrCreateDefaultConvert(Class<?> clazz) {
        return fact.getOrCreateDefaultConvert(clazz);
    }
}
