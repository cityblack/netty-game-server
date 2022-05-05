package com.lzh.game.start.config;

import com.lzh.game.socket.Request;
import com.lzh.game.socket.core.invoke.ConvertManager;
import com.lzh.game.socket.core.invoke.ParamConvert;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.start.model.player.Player;

import java.util.Set;

public class ConsumeConvertManager implements ConvertManager {

    private ConvertManager fact;

    public ConsumeConvertManager(ConvertManager fact) {
        this.fact = fact;
        registerInner();
    }

    private void registerInner() {
        this.registerConvert(Session.class, new SessionConvert());
        this.registerConvert(Request.class, new RequestConvert());
        this.registerConvert(Player.class, new PlayerConvert());
    }

    @Override
    public boolean registerConvert(Class<?> target, ParamConvert<?> convert, boolean inner) {
        return fact.registerConvert(target, convert, inner);
    }

    @Override
    public ParamConvert<?> getConvert(Class<?> clazz) {
        return fact.getConvert(clazz);
    }

    @Override
    public boolean isInnerConvert(Class<?> clazz) {
        return fact.isInnerConvert(clazz);
    }

    @Override
    public Set<Class<?>> inner() {
        return fact.inner();
    }

    @Override
    public boolean hasConvert(Class<?> target) {
        return fact.hasConvert(target);
    }
}
