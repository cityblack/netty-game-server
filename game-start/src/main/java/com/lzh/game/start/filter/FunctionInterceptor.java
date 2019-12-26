package com.lzh.game.start.filter;

import com.lzh.game.socket.dispatcher.action.ActionInterceptor;
import com.lzh.game.socket.exchange.Request;
import com.lzh.game.start.model.function.Function;
import com.lzh.game.start.model.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

@Component
@Slf4j
public class FunctionInterceptor implements ActionInterceptor {

    @Override
    public boolean intercept(Request request, Method method, Object[] param) {
        if (method.isAnnotationPresent(Function.class)) {
            Player player = getPlayerParam(param);
            if (Objects.isNull(player)) {
                log.error("功能开启拦截找不到player参数. {}", method.getName());
                return true;
            }
            int functionId = method.getAnnotation(Function.class).value();

        }
        return false;
    }

    private Player getPlayerParam(Object[] param) {
        for (Object o: param) {
            if (o instanceof Player) {
                return Objects.isNull(o) ? null : (Player)o;
            }
        }
        return null;
    }
}
