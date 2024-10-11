package com.lzh.game.start.filter;

import com.lzh.game.framework.common.method.EnhanceMethodInvoke;
import com.lzh.game.framework.socket.core.invoke.support.ActionInterceptor;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.start.model.function.Function;
import com.lzh.game.start.model.function.service.FunctionService;
import com.lzh.game.start.model.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class FunctionInterceptor implements ActionInterceptor {

    @Autowired
    private FunctionService functionService;

    private Player getPlayerParam(Object[] param) {
        for (Object o: param) {
            if (o instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    @Override
    public boolean intercept(Request request, EnhanceMethodInvoke invoke, Object[] param) {
        var method = invoke.method();
        if (method.isAnnotationPresent(Function.class)) {
            Player player = getPlayerParam(param);
            if (Objects.isNull(player)) {
                log.error("功能开启拦截找不到player参数. {}", method.getName());
                return true;
            }
            int functionId = method.getAnnotation(Function.class).value();
            boolean open = functionService.isOpen(player, functionId);
            if (!open && log.isDebugEnabled()) {
                log.debug("拦截玩家:{} 功能:{}", player.getKey(), functionId);
            }
            return open;
        }
        return false;
    }
}
