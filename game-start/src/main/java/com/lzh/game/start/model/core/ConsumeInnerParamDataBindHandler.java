package com.lzh.game.start.model.core;

import com.lzh.game.socket.dispatcher.action.convent.InnerParamDataBindHandler;
import com.lzh.game.socket.exchange.Request;
import com.lzh.game.socket.exchange.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.util.SpringContext;
import org.springframework.core.MethodParameter;

import java.util.*;
import java.util.function.Function;

public class ConsumeInnerParamDataBindHandler implements InnerParamDataBindHandler {

    private static final Map<Class<?>, Function<Request, Object>> INNER_PARAM = new HashMap<>(3);

    static {
        INNER_PARAM.put(Session.class, r -> r.getSession());
        INNER_PARAM.put(Request.class, r -> r);
        INNER_PARAM.put(Player.class, r -> SpringContext.singleTon().getPlayerService().getPlayer(r.getSession()));
    }

    @Override
    public Object conventData(Request request, MethodParameter parameter) {

        Function<Request, Object> fn = INNER_PARAM.get(parameter.getParameterType());
        if (Objects.isNull(fn)) {
            return null;
        }

        return fn.apply(request);
    }

    @Override
    public boolean isInnerParam(MethodParameter parameter) {

        return INNER_PARAM.containsKey(parameter.getParameterType());
    }

}
